package com.lumina.app_daymood.domain.models

data class HabitCategoryModel(
    val categoryId: Int,
    val categoryName: String,
    val habits: List<HabitModel>
)

class HabitModel(
    val id: String,
    val name: String,
    val categoryId: Int
)