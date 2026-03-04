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

    // Estados de UI
    var uploadState by mutableStateOf<UploadState>(UploadState.ImageNotSelected)
        private set

    var imageUri by mutableStateOf<Uri?>(null)
        private set

    // Campos del formulario — públicos para que la View los pueda leer y el Composable bindearlos
    var emotionName by mutableStateOf("")
    var selectedCategoryId by mutableStateOf(1)   // Int — id de la categoría
    var saveToFavorites by mutableStateOf(true)

    var isSubmitting by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var successMessage by mutableStateOf<String?>(null)
        private set

    // Imagen

    fun onImageSelected(uri: Uri?) {
        if (uri == null) return
        imageUri = uri
        simulateImageLoad()
    }

    private fun simulateImageLoad() {
        viewModelScope.launch {
            uploadState = UploadState.UploadingImage(0f)
            for (i in 1..10) {
                delay(200)
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

            // Paso 1: Subir imagen a Firebase Storage
            val userId = authRepository.getCurrentUser() ?: "mock_user"
            val token = authRepository.getIdToken() ?: ""

            val uploadResult = emotionRepository.uploadEmotionImage(userId, uri)
            if (uploadResult.isFailure) {
                errorMessage = "Error al subir imagen: ${uploadResult.exceptionOrNull()?.message}"
                isSubmitting = false
                return@launch
            }
            val imgUrl = uploadResult.getOrThrow()

            // Paso 2: Crear emoción en el backend (el backend manejará favoritos si saveToFavorites es true)
            val createResult = emotionRepository.createEmotion(
                token = token,
                name = emotionName.trim(),
                categoryId = selectedCategoryId,
                imgUrl = imgUrl,
                saveToFavorites = saveToFavorites
            )
            if (createResult.isFailure) {
                errorMessage = "Error al crear emoción: ${createResult.exceptionOrNull()?.message}"
                isSubmitting = false
                return@launch
            }

            isSubmitting = false
            successMessage = "¡Emoción subida correctamente!"
            resetForm()
            onSuccess()
        }
    }

    private fun resetForm() {
        imageUri = null
        uploadState = UploadState.ImageNotSelected
        emotionName = ""
        selectedCategoryId = 1
        saveToFavorites = true
    }

    fun clearMessages() {
        errorMessage = null
        successMessage = null
    }
}

