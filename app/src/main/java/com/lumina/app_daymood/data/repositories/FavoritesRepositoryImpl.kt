package com.lumina.app_daymood.data.repositories

import com.lumina.app_daymood.data.api.ApiService
import com.lumina.app_daymood.data.api.dto.FavoriteRequest
import com.lumina.app_daymood.domain.models.EmotionModel
import com.lumina.app_daymood.domain.repositories.IFavoritesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoritesRepositoryIml(
    private val apiService: ApiService
) : IFavoritesRepository {

    override suspend fun getFavorites(): Result<List<EmotionModel>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getFavorites()
            if (!response.success) throw Exception(response.message ?: "Error al obtener favoritos")
            
            Result.success(response.data.map { it.emotion.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUploadedEmotions(): Result<List<EmotionModel>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUploadedEmotions()
            if (!response.success) throw Exception("Error al obtener emociones subidas")
            Result.success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addFavorite(emotionId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.addFavorite(emotionId, FavoriteRequest(emotionId = emotionId))
            if (!response.success) throw Exception(response.message ?: "Error al actualizar favorito")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
