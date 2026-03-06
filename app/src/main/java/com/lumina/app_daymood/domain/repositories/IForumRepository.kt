package com.lumina.app_daymood.domain.repositories

import com.lumina.app_daymood.domain.models.CommentModel
import com.lumina.app_daymood.domain.models.PostModel

interface IForumRepository {
    suspend fun getPosts(token: String): Result<List<PostModel>>
    suspend fun createPost(
        token: String,
        forumId: String,
        categoryId: Int,
        title: String,
        content: String
    ): Result<PostModel>
    suspend fun updatePost(
        token: String,
        postId: String,
        title: String,
        content: String
    ): Result<PostModel>
    suspend fun deletePost(
        token: String,
        postId: String
    ): Result<Unit>
    suspend fun getComments(token: String, postId: String): Result<List<CommentModel>>
    suspend fun addComment(
        token: String,
        postId: String,
        content: String
    ): Result<List<CommentModel>>
    suspend fun deleteComment(
        token: String,
        commentId: String
    ): Result<Unit>
}
