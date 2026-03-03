package com.lumina.app_daymood.data.api.dto

import com.google.gson.annotations.SerializedName
import com.lumina.app_daymood.domain.models.PostModel

data class PostDTO(
    @SerializedName("id") val id: String,
    @SerializedName("id_user") val id_user: String,
    @SerializedName("id_forum") val id_forum: String,
    @SerializedName("id_category") val id_category: Int,
    @SerializedName("username") val username: String = "",
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("comments") val comments: List<CommentDTO> = emptyList()
) {
    fun toDomain(): PostModel = PostModel(
        id = id,
        id_user = id_user,
        id_forum = id_forum,
        id_category = id_category,
        username = username,
        title = title,
        content = content,
        comments = comments.map { it.toDomain() }
    )
}

// Single post response (create / get by id)
data class PostResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val data: PostDTO? = null
)

// List of posts response (getAllPosts)
data class PostsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val data: List<PostDTO> = emptyList()
)

data class PostRequest(
    @SerializedName("id_user") val userId: String,
    @SerializedName("id_forum") val forumId: String,
    @SerializedName("id_category") val id_category: Int,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String
)