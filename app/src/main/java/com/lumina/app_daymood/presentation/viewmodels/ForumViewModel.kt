package com.lumina.app_daymood.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.app_daymood.domain.models.CommentModel
import com.lumina.app_daymood.domain.models.PostModel
import com.lumina.app_daymood.domain.repositories.IAuthRepository
import com.lumina.app_daymood.domain.repositories.IForumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// De números de categorías a texto
val categoryMap = mapOf(
    // Foro
    16 to "Bienestar emocional",
    17 to "Estudios, trabajo y presión",
    18 to "Relaciones y vínculos",
    19 to "Autoconocimiento",
    20 to "Logros"
)

// Reverse: display name → id
val categoryIdByName: Map<String, Int> = categoryMap.entries.associate { (k, v) -> v to k }

// UI State

data class ForumUiState(
    val isLoading: Boolean = false,
    val posts: List<PostModel> = emptyList(),
    val error: String? = null,
    val selectedCategory: String = categoryMap[16] ?: "Bienestar emocional",
    val selectedCategoryId: Int = 16,
    val currentForumId: String? = null
)

data class CommentsUiState(
    val isLoading: Boolean = false,
    val comments: List<CommentModel> = emptyList(),
    val error: String? = null,
    val isSending: Boolean = false
)

data class CreatePostUiState(
    val isLoading: Boolean = false, val success: Boolean = false, val error: String? = null
)


class ForumViewModel(
    private val forumRepository: IForumRepository, private val authRepository: IAuthRepository
) : ViewModel() {

    companion object {
        private const val TAG = "ForumViewModel"
    }

    private val _forumState = MutableStateFlow(ForumUiState())
    val forumState: StateFlow<ForumUiState> = _forumState.asStateFlow()

    private val _commentsState = MutableStateFlow(CommentsUiState())
    val commentsState: StateFlow<CommentsUiState> = _commentsState.asStateFlow()

    private val _createPostState = MutableStateFlow(CreatePostUiState())
    val createPostState: StateFlow<CreatePostUiState> = _createPostState.asStateFlow()

    fun loadPostsByCategory(categoryId: Int) {
        viewModelScope.launch {
            val token = authRepository.getIdToken() ?: return@launch
            _forumState.update { it.copy(isLoading = true, error = null) }
            Log.d(TAG, "Cargando foro para categoría $categoryId (${categoryMap[categoryId]})")

            forumRepository.getForumIdForCategory(token, categoryId)
                .onSuccess { forumId ->
                    Log.d(TAG, "Paso 1 OK — forumId=$forumId para categoría $categoryId")
                    _forumState.update { it.copy(currentForumId = forumId) }

                    forumRepository.getForumDetail(token, forumId)
                        .onSuccess { posts ->
                            Log.d(TAG, "Paso 2 OK — ${posts.size} posts cargados")
                            _forumState.update { it.copy(isLoading = false, posts = posts) }
                        }
                        .onFailure { error ->
                            Log.e(TAG, "Error en paso 2 (detail): ${error.message}")
                            _forumState.update { it.copy(isLoading = false, error = error.message) }
                        }
                }
                .onFailure { error ->
                    Log.e(TAG, "Error en paso 1 (category): ${error.message}")
                    _forumState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun selectCategory(category: String) {
        val categoryId = categoryIdByName[category] ?: return
        _forumState.update { state ->
            state.copy(selectedCategory = category, selectedCategoryId = categoryId)
        }
        // Cada cambio de categoría dispara el flujo de 2 pasos
        loadPostsByCategory(categoryId)
    }

    //  Crear Post

    fun createPost(
        categoryName: String, title: String, content: String
    ) {
        val forumId = _forumState.value.currentForumId
        if (forumId == null) {
            _createPostState.update { it.copy(error = "No se ha cargado ningún foro") }
            return
        }
        val categoryId = categoryIdByName[categoryName] ?: run {
            _createPostState.update { it.copy(error = "Categoría inválida") }
            return
        }

        viewModelScope.launch {
            val token = authRepository.getIdToken() ?: return@launch
            _createPostState.update { it.copy(isLoading = true, success = false, error = null) }
            forumRepository.createPost(
                token = token,
                forumId = forumId,
                categoryId = categoryId,
                title = title,
                content = content
            ).onSuccess { newPost ->
                // Recargar posts del foro actual
                loadPostsByCategory(_forumState.value.selectedCategoryId)
                _createPostState.update { it.copy(isLoading = false, success = true) }
            }.onFailure { error ->
                _createPostState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    fun resetCreatePostState() {
        _createPostState.update { CreatePostUiState() }
    }

    // Edit/Delete Posts
    fun updatePost(postId: String, title: String, content: String) {
        viewModelScope.launch {
            val token = authRepository.getIdToken() ?: return@launch
            _forumState.update { it.copy(isLoading = true, error = null) }
            forumRepository.updatePost(token, postId, title, content).onSuccess {
                loadPostsByCategory(_forumState.value.selectedCategoryId)
            }.onFailure { error ->
                _forumState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            val token = authRepository.getIdToken() ?: return@launch
            _forumState.update { it.copy(isLoading = true, error = null) }
            forumRepository.deletePost(token, postId).onSuccess {
                loadPostsByCategory(_forumState.value.selectedCategoryId)
            }.onFailure { error ->
                _forumState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }


    //  Comments — se leen directo del post, solo crear y borrar necesitan API

    fun loadComments(postId: String) {
        // Los comentarios ya vienen en el post desde el detail del foro
        val post = _forumState.value.posts.find { it.id == postId }
        if (post != null) {
            _commentsState.update {
                it.copy(isLoading = false, comments = post.comments, error = null)
            }
        } else {
            _commentsState.update {
                it.copy(isLoading = false, error = "Post no encontrado")
            }
        }
    }

    fun addComment(postId: String, content: String) {
        viewModelScope.launch {
            val token = authRepository.getIdToken() ?: return@launch
            _commentsState.update { it.copy(isSending = true) }
            forumRepository.addComment(token, postId, content)
                .onSuccess { newComment ->
                    // Recargar el foro completo para obtener comentarios actualizados
                    val categoryId = _forumState.value.selectedCategoryId
                    val forumId = _forumState.value.currentForumId
                    if (forumId != null) {
                        forumRepository.getForumDetail(token, forumId)
                            .onSuccess { posts ->
                                _forumState.update { it.copy(posts = posts) }
                                // Actualizar los comentarios del post actual
                                val updatedPost = posts.find { it.id == postId }
                                _commentsState.update {
                                    it.copy(
                                        isSending = false,
                                        comments = updatedPost?.comments ?: emptyList()
                                    )
                                }
                            }
                            .onFailure {
                                // Si falla el refresh, al menos agregamos el nuevo
                                _commentsState.update { state ->
                                    state.copy(
                                        isSending = false,
                                        comments = state.comments + newComment
                                    )
                                }
                            }
                    } else {
                        _commentsState.update { state ->
                            state.copy(
                                isSending = false,
                                comments = state.comments + newComment
                            )
                        }
                    }
                }.onFailure { error ->
                    _commentsState.update { it.copy(isSending = false, error = error.message) }
                }
        }
    }

    fun deleteComment(postId: String, commentId: String) {
        viewModelScope.launch {
            val token = authRepository.getIdToken() ?: return@launch
            _commentsState.update { it.copy(isLoading = true) }
            forumRepository.deleteComment(token, commentId).onSuccess {
                // Recargar foro para refrescar comentarios
                val forumId = _forumState.value.currentForumId
                if (forumId != null) {
                    forumRepository.getForumDetail(token, forumId)
                        .onSuccess { posts ->
                            _forumState.update { it.copy(posts = posts) }
                            val updatedPost = posts.find { it.id == postId }
                            _commentsState.update {
                                it.copy(
                                    isLoading = false,
                                    comments = updatedPost?.comments ?: emptyList()
                                )
                            }
                        }
                        .onFailure {
                            _commentsState.update { it.copy(isLoading = false) }
                        }
                }
            }.onFailure { error ->
                _commentsState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    fun clearCommentsError() {
        _commentsState.update { it.copy(error = null) }
    }
}