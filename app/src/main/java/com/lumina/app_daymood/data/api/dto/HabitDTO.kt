package com.lumina.app_daymood.data.api.dto

import com.google.gson.annotations.SerializedName
import com.lumina.app_daymood.domain.models.HabitModel

data class HabitDTO(
    @SerializedName("id")          val id: String,
    @SerializedName("name")        val name: String,
    @SerializedName("id_category") val categoryId: Int      // INT en BD, y la API usará id_category
) {
    fun toDomain(): HabitModel = HabitModel(
        id = id,
        name = name,
        categoryId = categoryId
    )
}

data class HabitsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data")    val data: List<HabitDTO> = emptyList()
)