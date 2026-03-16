package com.lumina.app_daymood.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.app_daymood.domain.models.EmotionModel
import com.lumina.app_daymood.domain.repositories.IFavoritesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesRepository: IFavoritesRepository
) : ViewModel() {

    val favorites = mutableStateListOf<EmotionModel>()
    val uploadedEmotions = mutableStateListOf<EmotionModel>()

    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Cargar todo lo necesario para el Home en paralelo
    fun loadHomeData() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            
            // Lanzamos ambas peticiones al mismo tiempo
            val favsDeferred = async { favoritesRepository.getFavorites() }
            val uploadedDeferred = async { favoritesRepository.getUploadedEmotions() }

            val favsResult = favsDeferred.await()
            val uploadedResult = uploadedDeferred.await()

            isLoading = false

            favsResult.onSuccess { list ->
                favorites.clear()
                favorites.addAll(list)
            }.onFailure { e ->
                errorMessage = "Error en favoritos: ${e.message}"
            }

            uploadedResult.onSuccess { list ->
                uploadedEmotions.clear()
                uploadedEmotions.addAll(list)
            }.onFailure { e ->
                errorMessage = "Error en comunidad: ${e.message}"
            }
        }
    }

    // Funciones individuales por si se necesitan refrescar por separado
    fun loadFavorites() {
        viewModelScope.launch {
            favoritesRepository.getFavorites().onSuccess { list ->
                favorites.clear()
                favorites.addAll(list)
            }
        }
    }

    fun loadUploadedEmotions() {
        viewModelScope.launch {
            favoritesRepository.getUploadedEmotions().onSuccess { list ->
                uploadedEmotions.clear()
                uploadedEmotions.addAll(list)
            }
        }
    }

    fun addFavorite(emotionId: String) {
        viewModelScope.launch {
            favoritesRepository.addFavorite(emotionId).onSuccess {
                loadFavorites() // Refrescamos tras agregar
            }.onFailure { e ->
                errorMessage = "Error al agregar favorito: ${e.message}"
            }
        }
    }
}
