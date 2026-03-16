package com.lumina.app_daymood.domain.repositories

import android.net.Uri
import com.lumina.app_daymood.domain.models.EmotionModel

interface IEmotionRepository {
    suspend fun createEmotion(
        name: String,
        categoryId: Int,
        imageUri: Uri,
        saveToFavorites: Boolean
    ): Result<EmotionModel>

    suspend fun getUploadedEmotions(): Result<List<EmotionModel>>
}
