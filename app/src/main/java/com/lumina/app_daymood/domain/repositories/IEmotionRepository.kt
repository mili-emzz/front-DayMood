package com.lumina.app_daymood.domain.repositories

import android.net.Uri
import com.lumina.app_daymood.domain.models.EmotionModel

interface IEmotionRepository {
    /**
     * Crea una emoción personalizada enviando la imagen directamente a la API
     * como multipart/form-data. La API se encarga de subir a Firebase Storage.
     */
    suspend fun createEmotion(
        token: String,
        name: String,
        categoryId: Int,
        imageUri: Uri,
        saveToFavorites: Boolean
    ): Result<EmotionModel>
}