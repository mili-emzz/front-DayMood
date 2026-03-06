package com.lumina.app_daymood.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.app_daymood.domain.repositories.IAuthRepository
import com.lumina.app_daymood.domain.repositories.IFormRepository
import kotlinx.coroutines.launch

data class FormUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSubmitted: Boolean = false
)

class FormViewModel(
    private val formRepository: IFormRepository,
    private val authRepository: IAuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(FormUiState())
        private set

    /**
     * Envía las respuestas del formulario al API.
     * @param answers Mapa índice (0-9) → valor (1-5) proveniente de TmmsTestView
     * @param onSuccess Callback que se ejecuta al completar exitosamente
     */
    fun submitForm(answers: Map<Int, Int>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            val token = authRepository.getIdToken()
            if (token == null) {
                uiState = uiState.copy(isLoading = false, error = "No se pudo obtener el token")
                return@launch
            }

            // Convertir Map<Int, Int> (índice 0-9) → Map<String, Int> (q1..q10)
            val apiAnswers = answers.entries.associate { (index, value) ->
                "q${index + 1}" to value
            }

            Log.d("FormViewModel", "Enviando formulario: $apiAnswers")

            formRepository.submitForm(token, apiAnswers)
                .onSuccess {
                    Log.d("FormViewModel", "Formulario enviado exitosamente")
                    uiState = uiState.copy(isLoading = false, isSubmitted = true)
                    onSuccess()
                }
                .onFailure { error ->
                    Log.e("FormViewModel", "Error al enviar formulario: ${error.message}")
                    uiState = uiState.copy(
                        isLoading = false,
                        error = error.message ?: "Error al enviar el formulario"
                    )
                }
        }
    }
}
