package com.lumina.app_daymood.presentation.viewmodels

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

// De numeros de categorias a texto
val categoryMap = mapOf(
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
// filtrar por categoria, todos es sin filtro
    val selectedCategory: String = "Todos"
)

data class CommentsUiState(
    val isLoading: Boolean = false,
    val comments: List<CommentModel> = emptyList(),
    val error: String? = null,
    val isSending: Boolean = false
)

data class CreatePostUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

//  ViewModel 

class ForumViewModel(
    private val forumRepository: IForumRepository,
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _forumState = MutableStateFlow(ForumUiState())
    val forumState: StateFlow<ForumUiState> = _forumState.asStateFlow()

    private val _commentsState = MutableStateFlow(CommentsUiState())
    val commentsState: StateFlow<CommentsUiState> = _commentsState.asStateFlow()

    private val _createPostState = MutableStateFlow(CreatePostUiState())
    val createPostState: StateFlow<CreatePostUiState> = _createPostState.asStateFlow()

    // Holds the full unfiltered list so category filtering is done in-memory
    private var allPosts: List<PostModel> = emptyList()

    //  Forum / Posts 

    fun loadPosts() {
        val token = authRepository.getCurrentUser() ?: return
        viewModelScope.launch {
            _forumState.update { it.copy(isLoading = true, error = null) }
            forumRepository.getPosts(token)
                .onSuccess { posts ->
                    allPosts = posts
                    _forumState.update { state ->
                        state.copy(
                            isLoading = false,
                            posts = filterPosts(posts, state.selectedCategory)
                        )
                    }
                }
                .onFailure { error ->
                    _forumState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun selectCategory(category: String) {
        _forumState.update { state ->
            state.copy(
                selectedCategory = category,
                posts = filterPosts(allPosts, category)
            )
        }
    }

    private fun filterPosts(posts: List<PostModel>, category: String): List<PostModel> {
        if (category == "Todos") return posts
        val catId = categoryIdByName[category] ?: return posts
        return posts.filter { it.id_category == catId }
    }

    //  Crear Post 

    fun createPost(
        forumId: String,
        categoryName: String,
        title: String,
        content: String
    ) {
        val token = authRepository.getCurrentUser() ?: return
        val userId = token // ajustar si se almacena el id de usuario por separado
        val categoryId = categoryIdByName[categoryName] ?: run {
            _createPostState.update { it.copy(error = "Categoría inválida") }
            return
        }

        viewModelScope.launch {
            _createPostState.update { it.copy(isLoading = true, success = false, error = null) }
            forumRepository.createPost(
                token = token,
                userId = userId,
                forumId = forumId,
                categoryId = categoryId,
                title = title,
                content = content
            )
                .onSuccess { newPost ->
                    allPosts = listOf(newPost) + allPosts
                    _forumState.update { state ->
                        state.copy(posts = filterPosts(allPosts, state.selectedCategory))
                    }
                    _createPostState.update { it.copy(isLoading = false, success = true) }
                }
                .onFailure { error ->
                    _createPostState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun resetCreatePostState() {
        _createPostState.update { CreatePostUiState() }
    }

    //  Comments 

    fun loadComments(postId: String) {
        val token = authRepository.getCurrentUser() ?: return
        viewModelScope.launch {
            _commentsState.update { it.copy(isLoading = true, error = null) }
            forumRepository.getComments(token, postId)
                .onSuccess { comments ->
                    _commentsState.update { it.copy(isLoading = false, comments = comments) }
                }
                .onFailure { error ->
                    _commentsState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun addComment(postId: String, content: String) {
        val token = authRepository.getCurrentUser() ?: return
        val userId = token // ajustar  si se almacena el id de usuario por separado
        viewModelScope.launch {
            _commentsState.update { it.copy(isSending = true) }
            forumRepository.addComment(token, userId, postId, content)
                .onSuccess { updatedComments ->
                    _commentsState.update {
                        it.copy(isSending = false, comments = updatedComments)
                    }
                }
                .onFailure { error ->
                    _commentsState.update { it.copy(isSending = false, error = error.message) }
                }
        }
    }

    fun clearCommentsError() {
        _commentsState.update { it.copy(error = null) }
    }
}