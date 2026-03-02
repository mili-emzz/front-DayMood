package com.lumina.app_daymood.domain.models

data class ForumModel(
    val id: String,
    val min_age: Int,
    val max_age: Int,
    val id_category: Int,
    val users: List<UserModel>,
    val posts: List<PostModel>
)

data class PostModel(
    val id: String,
    val id_user: String,
    val id_forum: String,
    val title: String,
    val content: String,
    val comments: List<CommentModel>
)

data class CommentModel(
    val id: String,
    val content: String,
    val id_post: String,
    val id_user: String
)