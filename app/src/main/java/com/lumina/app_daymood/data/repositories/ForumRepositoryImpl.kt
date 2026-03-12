package com.lumina.app_daymood.data.repositories

import android.util.Log
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

    companion object {
        private const val TAG = "ForumRepository"
    }

    override suspend fun getForumIdForCategory(token: String, categoryId: Int): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val forums = apiService.getForumsByCategory("Bearer $token", categoryId)
                if (forums.isEmpty()) {
                    throw Exception("No se encontró un foro para la categoría $categoryId")
                }
                val forum = forums.first()
                Log.d(TAG, "Foro encontrado para categoría $categoryId: id=${forum.id}, rango de edad=${forum.min_age}-${forum.max_age}")
                Result.success(forum.id)
            } catch (e: Exception) {
                Log.e(TAG, "Error obteniendo foro para categoría $categoryId: ${e.message}")
                Result.failure(e)
            }
        }

    override suspend fun getForumDetail(token: String, forumId: String): Result<List<PostModel>> =
        withContext(Dispatchers.IO) {
            try {
                val forum = apiService.getForumDetail("Bearer $token", forumId)
                // Usamos la lista segura mapeada para evitar el error de IterablesKt
                val domainPosts = forum.posts?.map { it.toDomain() } ?: emptyList()
                
                Log.d(TAG, "Detalle de foro cargado: ${domainPosts.size} posts, rango de edad=${forum.min_age}-${forum.max_age}")
                Result.success(domainPosts)
            } catch (e: Exception) {
                Log.e(TAG, "Error obteniendo detalle del foro $forumId: ${e.message}")
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
            Log.d(TAG, "Creando post en forumId: $forumId, cat: $categoryId")
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
            Log.e(TAG, "Error creando post: ${e.message}")
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

    override suspend fun addComment(
        token: String,
        postId: String,
        content: String
    ): Result<CommentModel> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.addComment(
                token = "Bearer $token",
                request = CommentRequest(
                    postId = postId,
                    content = content
                )
            )
            Result.success(response.toDomain())
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
