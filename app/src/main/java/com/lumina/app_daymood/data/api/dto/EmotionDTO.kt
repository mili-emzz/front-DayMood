package com.lumina.app_daymood.data.api.dto

import com.google.gson.annotations.SerializedName
import com.lumina.app_daymood.domain.models.EmotionModel

data class EmotionDTO(
    @SerializedName("id")          val id: String,
    @SerializedName("name")        val name: String,
    @SerializedName("img_url")     val imgUrl: String,
    @SerializedName("id_category") val categoryId: Int,     // INT en BD, y la API usará id_category
    @SerializedName("id_user")     val userId: String? = null
) {
    fun toDomain(): EmotionModel = EmotionModel(
        id = id,
        name = name,
        imgUrl = imgUrl,
        categoryId = categoryId,
        userId = userId
    )
}

data class EmotionsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data")    val data: List<EmotionDTO> = emptyList()
)