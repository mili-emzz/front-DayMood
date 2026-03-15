package com.lumina.app_daymood.domain.repositories

import com.lumina.app_daymood.domain.models.CommentModel
import com.lumina.app_daymood.domain.models.PostModel

interface IForumRepository {
    suspend fun getForumIdForCategory(categoryId: Int): Result<String>

    suspend fun getForumDetail(forumId: String): Result<List<PostModel>>

    suspend fun createPost(
        forumId: String,
        categoryId: Int,
        title: String,
        content: String
    ): Result<PostModel>

    suspend fun updatePost(
        postId: String,
        title: String,
        content: String
    ): Result<PostModel>

    suspend fun deletePost(
        postId: String
    ): Result<Unit>

    suspend fun addComment(
        postId: String,
        content: String
    ): Result<CommentModel>

    suspend fun deleteComment(
        commentId: String
    ): Result<Unit>
}
