package com.lumina.app_daymood.domain.repositories

import com.lumina.app_daymood.domain.models.EmotionModel

interface IFavoritesRepository {
    suspend fun getFavorites(token: String): Result<List<EmotionModel>>
    suspend fun addFavorite(token: String, emotionId: String): Result<Unit>
}
