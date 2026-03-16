package com.lumina.app_daymood.data.repositories

import android.content.Context
import android.net.Uri
import com.lumina.app_daymood.data.api.ApiService
import com.lumina.app_daymood.data.firebase.FirebaseAuthDataSource
import com.lumina.app_daymood.domain.models.EmotionModel
import com.lumina.app_daymood.domain.repositories.IEmotionRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class EmotionRepositoryImpl(
    private val apiService: ApiService,
    private val context: Context,
) : IEmotionRepository {

    override suspend fun createEmotion(
        name: String,
        categoryId: Int,
        imageUri: Uri,
        saveToFavorites: Boolean
    ): Result<EmotionModel> {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: return Result.failure(Exception("No se pudo leer la imagen seleccionada"))
            val imageBytes = inputStream.readBytes()
            inputStream.close()

            val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"

            val imageBody = imageBytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", "emotion.jpg", imageBody)

            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryBody = categoryId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val favoritesBody = saveToFavorites.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            // El token se inyecta automáticamente vía AuthInterceptor
            val response = apiService.createEmotion(
                name = nameBody,
                categoryId = categoryBody,
                saveToFavorites = favoritesBody,
                image = imagePart
            )

            if (!response.success) throw Exception(response.message ?: "Error al crear emoción")
            val emotion = response.data?.toDomain() ?: throw Exception("No se recibió data")
            Result.success(emotion)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUploadedEmotions(): Result<List<EmotionModel>> {
        return try {
            val response = apiService.getUploadedEmotions()
            Result.success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
