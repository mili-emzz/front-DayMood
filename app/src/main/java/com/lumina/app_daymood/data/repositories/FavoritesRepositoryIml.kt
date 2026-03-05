package com.lumina.app_daymood.data.repositories

import com.lumina.app_daymood.data.api.ApiService
import com.lumina.app_daymood.data.api.dto.FavoriteRequest
import com.lumina.app_daymood.domain.models.EmotionModel
import com.lumina.app_daymood.domain.repositories.IFavoritesRepository

class FavoritesRepositoryIml(
    private val apiService: ApiService
): IFavoritesRepository {

    override suspend fun getFavorites(token: String): Result<List<EmotionModel>> {
        return try {
            val response = apiService.getFavorites("Bearer $token")
            if (!response.success) throw Exception(response.message ?: "Error al obtener favoritos")
            // La API devuelve una lista de FavoriteItemDTO con { id_emotion, id_user, emotions: {...} }
            Result.success(response.data.map { it.emotion.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addFavorite(
        token: String,
        emotionId: String
    ): Result<Unit> {
        return try{
            val response = apiService.addFavorite(
                token ="Bearer $token",
                request = FavoriteRequest(emotionId = emotionId)
            )
            if (!response.success) throw Exception(response.message ?: "Error al agregar a favoritos")
            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}