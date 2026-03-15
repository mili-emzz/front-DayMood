package com.lumina.app_daymood.data.repositories

import com.lumina.app_daymood.data.api.ApiService
import com.lumina.app_daymood.data.api.dto.CommentRequest
import com.lumina.app_daymood.data.api.dto.ForumCategoryDetailDTO
import com.lumina.app_daymood.data.api.dto.PostRequest
import com.lumina.app_daymood.data.api.dto.UpdatePostRequest
import com.lumina.app_daymood.domain.models.ForumModel
import com.lumina.app_daymood.domain.models.PostModel
import com.lumina.app_daymood.domain.repositories.IForumRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ForumRepositoryImpl(
    private val apiService: ApiService
) : IForumRepository {

    override suspend fun getForumIdForCategory(categoryId: Int): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val forums = apiService.getForumsByCategory(categoryId)

                val response = apiService.getForumsByCategory(categoryId)
                Result.success(response.map { it })
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getForumDetail(forumId: String): Result<List<PostModel>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getForumDetail(forumId)
                Result.success(response.toDomain())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }


    override suspend fun createPost(
        forumId: String,
        categoryId: Int,
        title: String,
        content: String,
    ): Result<PostModel> = withContext(Dispatchers.IO) {
        try {
            val request = PostRequest(
                forumId = forumId,
                id_category = categoryId,
                title = title,
                content = content,
            )
            val response = apiService.createPost(request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePost(
        postId: String,
        title: String,
        content: String
    ): Result<PostModel> = withContext(Dispatchers.IO) {
        try {
            val request = UpdatePostRequest(title = title, content = content)
            val response = apiService.updatePost(postId, request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePost(postId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            apiService.deletePost(postId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addComment(postId: String, content: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val request = CommentRequest(postId = postId, content = content)
                apiService.addComment(request)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun deleteComment(commentId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                apiService.deleteComment(commentId)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
