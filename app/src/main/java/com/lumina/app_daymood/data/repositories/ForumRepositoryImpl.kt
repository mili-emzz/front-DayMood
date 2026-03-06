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
        forumId: String,
        categoryId: Int,
        title: String,
        content: String
    ): Result<PostModel> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createPost(
                token = "Bearer $token",
                request = PostRequest(
                    forumId = forumId,
                    id_category = categoryId,
                    title = title,
                    content = content
                )
            )
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePost(
        token: String,
        postId: String,
        title: String,
        content: String
    ): Result<PostModel> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updatePost(
                token = "Bearer $token",
                postId = postId,
                request = com.lumina.app_daymood.data.api.dto.UpdatePostRequest(
                    title = title,
                    content = content
                )
            )
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePost(
        token: String,
        postId: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            apiService.deletePost("Bearer $token", postId)
            Result.success(Unit)
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

    // El API retorna { "id": "...", "content": "...", "id_post": "...", "created_at": "..." } -> un CommentDTO directo
    override suspend fun addComment(
        token: String,
        postId: String,
        content: String
    ): Result<List<CommentModel>> = withContext(Dispatchers.IO) {
        try {
            // Se hace la creacion
            val response = apiService.addComment(
                token = "Bearer $token",
                request = CommentRequest(
                    postId = postId,
                    content = content
                )
            )
            // Despues de crear, pedimos la lista de nuevo, o retornamos la nueva agregada al estado.
            // Pidiendo la lista actualizada
            val listResponse = apiService.getComments("Bearer $token", postId)
            if (!listResponse.success) throw Exception("Error al refrescar comentarios tras crearlo")
            Result.success(listResponse.data.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteComment(
        token: String,
        commentId: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            apiService.deleteComment("Bearer $token", commentId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
