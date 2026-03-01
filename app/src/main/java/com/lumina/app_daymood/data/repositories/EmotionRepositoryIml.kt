package com.lumina.app_daymood.data.repositories

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.lumina.app_daymood.data.api.ApiService
import com.lumina.app_daymood.data.api.dto.CreateEmotionRequest
import com.lumina.app_daymood.domain.models.EmotionModel
import com.lumina.app_daymood.domain.repositories.IEmotionRepository
import kotlinx.coroutines.tasks.await
import java.util.UUID

class EmotionRepositoryIml(
    private val apiService: ApiService,
    private val storage: FirebaseStorage
) : IEmotionRepository {

    override suspend fun uploadEmotionImage(userId: String, imageUri: Uri): Result<String> {
        return try {
            val imageId = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("emotions/$userId/$imageId.jpg")
            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createEmotion(
        token: String,
        name: String,
        categoryId: Int,
        imgUrl: String,
        saveToFavorites: Boolean
    ): Result<EmotionModel> {
        return try {
            val response = apiService.createEmotion(
                token = "Bearer $token",
                request = CreateEmotionRequest(
                    name = name,
                    imgUrl = imgUrl,
                    categoryId = categoryId,
                    saveToFavorites = saveToFavorites
                )
            )
            if (!response.success) throw Exception(response.message ?: "Error al crear emoción")
            val emotion = response.data?.toDomain() ?: throw Exception("No se recibió data")
            Result.success(emotion)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
