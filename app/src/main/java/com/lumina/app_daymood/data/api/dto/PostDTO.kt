package com.lumina.app_daymood.data.api.dto

import com.google.gson.annotations.SerializedName
import com.lumina.app_daymood.domain.models.CommentModel
import com.lumina.app_daymood.domain.models.PostModel

data class NestedUserDTO(
    @SerializedName("username") val username: String,
    @SerializedName("birth_day") val birthDay: String? = null
)

data class PostDTO(
    @SerializedName("id") val id: String,
    @SerializedName("id_user") val id_user: String,
    @SerializedName("id_forum") val id_forum: String,
    @SerializedName("id_category") val id_category: Int,
    @SerializedName("users") val user: NestedUserDTO? = null,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("comments") val comments: List<CommentDTO>? = emptyList()
) {
    fun toDomain(): PostModel = PostModel(
        id = id,
        id_user = id_user,
        id_forum = id_forum,
        id_category = id_category,
        username = user?.username ?: "Anónimo",
        title = title,
        content = content,
        comments = comments?.map { it.toDomain() } ?: emptyList()
    )
}

data class PostRequest(
    @SerializedName("id_forum") val forumId: String,
    @SerializedName("id_category") val id_category: Int,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String
)

data class UpdatePostRequest(
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String
)