package com.lumina.app_daymood.data.api.dto

import com.google.gson.annotations.SerializedName
import com.lumina.app_daymood.domain.models.HabitCategoryModel
import com.lumina.app_daymood.domain.models.HabitModel

data class HabitCategoryDTO(
    @SerializedName("id_category") val categoryId: Int,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("habits") val habits: List<HabitDTO> = emptyList()
) {
    fun toDomain(): HabitCategoryModel = HabitCategoryModel(
        categoryId = categoryId,
        categoryName = categoryName,
        habits = habits.map { it.toDomain(categoryId) }
    )
}

data class HabitDTO(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("id_category") val categoryId: Int? = null // Optional fallback if the API sends it inside habit
) {
    fun toDomain(parentCategoryId: Int? = null): HabitModel = HabitModel(
        id = id,
        name = name,
        categoryId = categoryId ?: parentCategoryId ?: 0
    )
}

data class HabitsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<HabitCategoryDTO> = emptyList()
)