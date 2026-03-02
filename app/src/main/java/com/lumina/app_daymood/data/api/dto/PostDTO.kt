package com.lumina.app_daymood.data.api.dto

import com.google.gson.annotations.SerializedName
import com.lumina.app_daymood.domain.models.PostModel

data class PostDTO(
    @SerializedName("id") val id: String,
    @SerializedName("id_user") val id_user: String,
    @SerializedName("id_forum") val id_forum: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("comments") val comments: List<CommentDTO>
) {
    fun toDomain(): PostModel = PostModel(
        id = id,
        id_user = id_user,
        id_forum = id_forum,
        title = title,
        content = content,
        comments = comments.map { it.toDomain() }
    )
}

data class PostResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val data: PostDTO? = null
)

data class PostRequest(
    @SerializedName("id_user") val userId: String,
    @SerializedName("id_forum") val forumId: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String
)