package com.lumina.app_daymood.data.repositories

import com.lumina.app_daymood.data.api.ApiService
import com.lumina.app_daymood.domain.repositories.IStatsRepository
import com.lumina.app_daymood.presentation.views.stats.EmotionStat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StatsRepositoryImpl(
    private val apiService: ApiService
) : IStatsRepository {

    override suspend fun getWeeklyStats(): Result<List<EmotionStat>> =
        withContext(Dispatchers.IO) {
            try {
                // El token se inyecta automáticamente vía AuthInterceptor
                val response = apiService.getWeeklyStats()
                if (response.success && response.data != null) {
                    val grouped = response.data
                        .groupBy { it.emotion }
                        .map { (emotion, items) ->
                            EmotionStat(
                                label = emotion.replaceFirstChar { it.uppercase() },
                                count = items.sumOf { it.total }
                            )
                        }
                        .sortedByDescending { it.count }
                    Result.success(grouped)
                } else {
                    Result.failure(Exception(response.message ?: "Error al obtener estadísticas"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
