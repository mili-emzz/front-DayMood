package com.lumina.app_daymood.domain.repositories

import com.lumina.app_daymood.presentation.views.stats.EmotionStat

interface IStatsRepository {
    suspend fun getWeeklyStats(token: String): Result<List<EmotionStat>>
}
