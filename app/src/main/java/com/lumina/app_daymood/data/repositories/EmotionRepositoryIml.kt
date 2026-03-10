package com.lumina.app_daymood.data.repositories

import android.content.Context
import android.net.Uri
import com.lumina.app_daymood.data.api.ApiService
import com.lumina.app_daymood.domain.models.EmotionModel
import com.lumina.app_daymood.domain.repositories.IEmotionRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class EmotionRepositoryIml(
    private val apiService: ApiService,
    private val context: Context          // necesario para abrir el InputStream del Uri
) : IEmotionRepository {

    override suspend fun createEmotion(
        token: String,
        name: String,
        categoryId: Int,
        imageUri: Uri,
        saveToFavorites: Boolean
    ): Result<EmotionModel> {
        return try {
            // Leer bytes del Uri seleccionado por el usuario
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: return Result.failure(Exception("No se pudo leer la imagen seleccionada"))
            val imageBytes = inputStream.readBytes()
            inputStream.close()

            // Detectar MIME type (image/jpeg, image/png, etc.)
            val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"

            // Construir la parte "image" del multipart
            val imageBody = imageBytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", "emotion.jpg", imageBody)

            // Campos de texto como RequestBody
            val nameBody          = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryBody      = categoryId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val favoritesBody     = saveToFavorites.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.createEmotion(
                token          = "Bearer $token",
                name           = nameBody,
                categoryId     = categoryBody,
                saveToFavorites = favoritesBody,
                image          = imagePart
            )

            if (!response.success) throw Exception(response.message ?: "Error al crear emoción")
            val emotion = response.data?.toDomain() ?: throw Exception("No se recibió data")
            Result.success(emotion)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
