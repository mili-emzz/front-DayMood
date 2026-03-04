package com.lumina.app_daymood.data.repositories

import com.lumina.app_daymood.data.api.ApiService
import com.lumina.app_daymood.data.api.dto.CommentRequest
import com.lumina.app_daymood.data.api.dto.PostRequest
import com.lumina.app_daymood.domain.models.CommentModel
import com.lumina.app_daymood.domain.models.PostModel
import com.lumina.app_daymood.domain.repositories.IForumRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ForumRepositoryImpl(
    private val apiService: ApiService
) : IForumRepository {

    override suspend fun getPosts(token: String): Result<List<PostModel>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllPosts("Bearer $token")
                if (!response.success) throw Exception(response.message.ifBlank { "Error al obtener posts" })
                Result.success(response.data.map { it.toDomain() })
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun createPost(
        token: String,
        userId: String,
        forumId: String,
        categoryId: Int,
        title: String,
        content: String
    ): Result<PostModel> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createPost(
                token = "Bearer $token",
                request = PostRequest(
                    userId = userId,
                    forumId = forumId,
                    id_category = categoryId,
                    title = title,
                    content = content
                )
            )
            if (!response.success) throw Exception(response.message.ifBlank { "Error al crear post" })
            val post = response.data?.toDomain() ?: throw Exception("No se recibió data del post")
            Result.success(post)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getComments(token: String, postId: String): Result<List<CommentModel>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getComments("Bearer $token", postId)
                if (!response.success) throw Exception("Error al obtener comentarios")
                Result.success(response.data.map { it.toDomain() })
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun addComment(
        token: String,
        userId: String,
        postId: String,
        content: String
    ): Result<List<CommentModel>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.addComment(
                token = "Bearer $token",
                request = CommentRequest(
                    userId = userId,
                    postId = postId,
                    content = content
                )
            )
            if (!response.success) throw Exception("Error al agregar comentario")
            Result.success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
