package com.lumina.app_daymood.domain.models

data class Emotion(
    val id: String = "",
    val name: String = "",
    val imgUrl: String = "",
    val category: CategoryReference = CategoryReference()
)