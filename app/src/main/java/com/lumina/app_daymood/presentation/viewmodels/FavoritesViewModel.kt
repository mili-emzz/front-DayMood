package com.lumina.app_daymood.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.app_daymood.domain.models.EmotionModel
import com.lumina.app_daymood.domain.repositories.IAuthRepository
import com.lumina.app_daymood.domain.repositories.IFavoritesRepository
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesRepository: IFavoritesRepository,
    private val authRepository: IAuthRepository
) : ViewModel() {

    val favorites = mutableStateListOf<EmotionModel>()

    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Cargar favoritos

    fun loadFavorites() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val token = "mock_token" // TODO: obtener el token real del authRepository

            val result = favoritesRepository.getFavorites(token)
            isLoading = false
            result
                .onSuccess { list ->
                    favorites.clear()
                    favorites.addAll(list)
                }
                .onFailure { e ->
                    errorMessage = "Error al cargar favoritos: ${e.message}"
                }
        }
    }

    fun addFavorite(emotionId: String) {
        viewModelScope.launch {
            val token = "mock_token" // TODO: token real
            val result = favoritesRepository.addFavorite(token, emotionId)
            result.onFailure { e ->
                errorMessage = "Error al agregar favorito: ${e.message}"
            }
        }
    }

    fun isFavorite(emotionId: String): Boolean {
        return favorites.any { it.id == emotionId }
    }

    fun clearError() {
        errorMessage = null
    }
}
