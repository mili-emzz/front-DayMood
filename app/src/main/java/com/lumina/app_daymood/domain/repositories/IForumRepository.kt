package com.lumina.app_daymood.domain.repositories

import com.lumina.app_daymood.domain.models.CommentModel
import com.lumina.app_daymood.domain.models.PostModel

interface IForumRepository {
    suspend fun getPosts(token: String): Result<List<PostModel>>
    suspend fun createPost(
        token: String,
        userId: String,
        forumId: String,
        categoryId: Int,
        title: String,
        content: String
    ): Result<PostModel>
    suspend fun getComments(token: String, postId: String): Result<List<CommentModel>>
    suspend fun addComment(
        token: String,
        userId: String,
        postId: String,
        content: String
    ): Result<List<CommentModel>>
}
