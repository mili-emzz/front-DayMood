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
            val token = authRepository.getIdToken() ?: return@launch

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
            val token = authRepository.getIdToken() ?: return@launch
            val result = favoritesRepository.addFavorite(token, emotionId)
            result
                .onSuccess {
                    // Refrescamos la lista de favoritos
                    loadFavorites()
                }
                .onFailure { e ->
                    errorMessage = "Error al agregar favorito: ${e.message}"
                }
        }
    }
}
