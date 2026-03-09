package com.lumina.app_daymood.domain.repositories

import com.lumina.app_daymood.domain.models.CommentModel
import com.lumina.app_daymood.domain.models.PostModel

interface IForumRepository {
    // Paso 1: obtener el id del foro según categoría (el backend filtra por edad)
    suspend fun getForumIdForCategory(token: String, categoryId: Int): Result<String>

    // Paso 2: obtener los posts del foro (con comentarios anidados)
    suspend fun getForumDetail(token: String, forumId: String): Result<List<PostModel>>

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
    // Comentarios: crear y borrar (la lectura viene del detail)
    suspend fun addComment(
        token: String,
        postId: String,
        content: String
    ): Result<CommentModel>
    suspend fun deleteComment(
        token: String,
        commentId: String
    ): Result<Unit>
}
