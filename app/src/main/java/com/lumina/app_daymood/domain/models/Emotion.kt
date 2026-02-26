package com.lumina.app_daymood.domain.models

data class EmotionModel(
    val id: String,
    val name: String,
    val imgUrl: String,
    val categoryId: Int,
    val userId: String? = null
) {
    val isCustom: Boolean get() = userId != null
}