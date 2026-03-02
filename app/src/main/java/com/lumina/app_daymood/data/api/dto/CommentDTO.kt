package com.lumina.app_daymood.data.api.dto

import com.google.gson.annotations.SerializedName
import com.lumina.app_daymood.domain.models.CommentModel

data class CommentDTO(
    @SerializedName("id")   val id: String,
    @SerializedName("content") val content: String,
    @SerializedName("id_post")   val id_post: String,
    @SerializedName("id_user")   val id_user: String
){
    fun toDomain(): CommentModel = CommentModel(
        id = id,
        content = content,
        id_post = id_post,
        id_user = id_user
    )
}

data class CommentsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data")    val data: List<CommentDTO> = emptyList()
)

data class CommentRequest(
    @SerializedName("id_user")   val userId: String,
    @SerializedName("id_post")   val postId: String,
    @SerializedName("content") val content: String
)