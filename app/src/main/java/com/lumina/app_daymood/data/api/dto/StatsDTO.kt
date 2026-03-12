package com.lumina.app_daymood.data.api.dto

data class WeeklyStatItem(
    val id_user: String,
    val week_start: String,
    val emotion: String,
    val total: Int
)

data class WeeklyStatsResponse(
    val success: Boolean,
    val data: List<WeeklyStatItem>? = null,
    val message: String? = null
)
