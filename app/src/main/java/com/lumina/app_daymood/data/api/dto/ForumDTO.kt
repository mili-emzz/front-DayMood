package com.lumina.app_daymood.data.api.dto

import com.google.gson.annotations.SerializedName
import com.lumina.app_daymood.domain.models.ForumModel
import com.lumina.app_daymood.domain.models.RecordModel

data class ForumDTO(
    @SerializedName("id")          val id: String,
    @SerializedName("min_age")     val min_age: Int,
    @SerializedName("max_age")     val max_age: Int,
    @SerializedName("id_category") val category_id: Int,
    @SerializedName("users")       val users: List<UserData>,
    @SerializedName("posts")       val posts: List<PostDTO>
) {
    fun toDomain(userId: String): ForumModel = ForumModel(
        id = id,
        min_age = min_age,
        max_age = max_age,
        id_category = category_id,
        users = TODO(),
        posts = TODO(),
    )
}

data class EmotionsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data")    val data: List<EmotionDTO> = emptyList()
)

// Request con adivinanza para crear una emoción personalizada
data class CreateEmotionRequest(
    @SerializedName("name")        val name: String,
    @SerializedName("img_url")     val imgUrl: String,
    @SerializedName("id_category") val categoryId: Int,
    @SerializedName("save_to_favorites") val saveToFavorites: Boolean
)

data class CreateEmotionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data")    val data: EmotionDTO?,
    @SerializedName("message") val message: String? = null
)

// Request para agregar/eliminar favorito
data class FavoriteRequest(
    @SerializedName("id_emotion") val emotionId: String
)

data class FavoritesResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data")    val data: List<EmotionDTO> = emptyList(),
    @SerializedName("message") val message: String? = null
)

data class FavoriteActionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null
)