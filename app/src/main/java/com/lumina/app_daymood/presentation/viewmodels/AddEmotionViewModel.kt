package com.lumina.app_daymood.presentation.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.app_daymood.domain.repositories.IAuthRepository
import com.lumina.app_daymood.domain.repositories.IEmotionRepository
import com.lumina.app_daymood.presentation.views.add_emotion.UploadState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddEmotionViewModel(
    private val emotionRepository: IEmotionRepository,
    private val authRepository: IAuthRepository
) : ViewModel() {

    // Estado de UI
    var uploadState by mutableStateOf<UploadState>(UploadState.ImageNotSelected)
        private set

    var imageUri by mutableStateOf<Uri?>(null)
        private set

    // Campos del formulario
    var emotionName by mutableStateOf("")
    var selectedCategoryId by mutableStateOf(8)  // default: Alegría (id=8)
    var saveToFavorites by mutableStateOf(true)

    var isSubmitting by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var successMessage by mutableStateOf<String?>(null)
        private set

    fun onImageSelected(uri: Uri?) {
        if (uri == null) return
        imageUri = uri
        // Simula progress visual mientras el usuario llena el formulario
        simulateLoadingAnimation()
    }

    private fun simulateLoadingAnimation() {
        viewModelScope.launch {
            uploadState = UploadState.UploadingImage(0f)
            for (i in 1..10) {
                delay(150)
                uploadState = UploadState.UploadingImage(i / 10f)
            }
            uploadState = UploadState.UploadCompleted
        }
    }

    fun removeImage() {
        imageUri = null
        emotionName = ""
        uploadState = UploadState.ImageNotSelected
    }

    fun submitEmotion(onSuccess: () -> Unit) {
        val uri = imageUri ?: run {
            errorMessage = "Selecciona una imagen primero"
            return
        }
        if (emotionName.isBlank()) {
            errorMessage = "El nombre no puede estar vacío"
            return
        }
        if (selectedCategoryId <= 0) {
            errorMessage = "Selecciona una categoría"
            return
        }

        viewModelScope.launch {
            isSubmitting = true
            errorMessage = null

            val token = authRepository.getIdToken() ?: run {
                errorMessage = "No se pudo obtener el token de sesión"
                isSubmitting = false
                return@launch
            }

            // Un solo paso: mandar imagen + datos a la API (multipart)
            val result = emotionRepository.createEmotion(
                name            = emotionName.trim(),
                categoryId      = selectedCategoryId,
                imageUri        = uri,
                saveToFavorites = saveToFavorites
            )

            isSubmitting = false
            if (result.isFailure) {
                errorMessage = "Error al subir emoción: ${result.exceptionOrNull()?.message}"
                return@launch
            }

            successMessage = "¡Emoción subida correctamente!"
            resetForm()
            onSuccess()
        }
    }

    private fun resetForm() {
        imageUri = null
        uploadState = UploadState.ImageNotSelected
        emotionName = ""
        selectedCategoryId = 8
        saveToFavorites = true
    }

    fun clearMessages() {
        errorMessage = null
        successMessage = null
    }
}
