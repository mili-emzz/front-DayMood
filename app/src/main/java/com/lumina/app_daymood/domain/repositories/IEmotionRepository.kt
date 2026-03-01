package com.lumina.app_daymood.domain.repositories

import android.net.Uri
import com.lumina.app_daymood.domain.models.EmotionModel

interface IEmotionRepository {
    suspend fun uploadEmotionImage(userId: String, imageUri: Uri): Result<String>
    suspend fun createEmotion(
        token: String,
        name: String,
        categoryId: Int,
        imgUrl: String,
        saveToFavorites: Boolean
    ): Result<EmotionModel>
}