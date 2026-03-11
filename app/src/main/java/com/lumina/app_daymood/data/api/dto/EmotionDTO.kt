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

// Request para crear emoción personalizada se envía como multipart/form-data
data class CreateEmotionRequest (
    val name: String,
    val categoryId: Int,
    val saveToFavorites: Boolean
)

data class CreateEmotionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data")    val data: EmotionDTO?,
    @SerializedName("message") val message: String? = null
)

data class FavoriteRequest(
    @SerializedName("id_emotion") val emotionId: String
)

// Wrapper que devuelve la API en GET /api/emotions/favorites
data class FavoriteItemDTO(
    @SerializedName("id_emotion") val emotionId: String,
    @SerializedName("id_user")    val userId: String,
    @SerializedName("emotions")   val emotion: EmotionDTO
)

data class FavoritesResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data")    val data: List<FavoriteItemDTO> = emptyList(),
    @SerializedName("message") val message: String? = null
)

data class FavoriteActionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null
)