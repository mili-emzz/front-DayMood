package com.lumina.app_daymood.domain.repositories

import com.lumina.app_daymood.domain.models.EmotionModel

interface IFavoritesRepository {
    suspend fun getFavorites(): Result<List<EmotionModel>>
    suspend fun getUploadedEmotions(): Result<List<EmotionModel>>
    suspend fun addFavorite(emotionId: String): Result<Unit>
}
