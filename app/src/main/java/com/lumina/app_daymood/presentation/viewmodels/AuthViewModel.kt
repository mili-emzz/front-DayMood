package com.lumina.app_daymood.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.app_daymood.domain.models.UserModel
import com.lumina.app_daymood.domain.repositories.IAuthRepository
import kotlinx.coroutines.launch
data class AuthUiState(
    val isLoading: Boolean = false,
    val user: UserModel? = null,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)
class AuthViewModel(
    private val authRepository: IAuthRepository
) : ViewModel() {
    var uiState by mutableStateOf(AuthUiState())
        private set

    init {
        // Si ya hay sesión activa al abrir la app, cargamos los datos del usuario
        if (authRepository.isAuthenticated()) {
            loadCurrentUser()
        }
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            authRepository.loadCurrentUser()
                .onSuccess { user ->
                    uiState = uiState.copy(isLoading = false, user = user, isAuthenticated = true)
                }
                .onFailure { error ->
                    Log.e("AuthViewModel", "Error cargando usuario: ${error.message}")
                    uiState = uiState.copy(isLoading = false)
                }
        }
    }

    private fun getErrorMessage(error: Throwable): String {
        val message = error.message?.lowercase() ?: ""
        return when {
            message.contains("already-in-use") || message.contains("existe") ->
                "Este email ya está registrado. Intenta iniciar sesión."
            message.contains("password") -> "Contraseña muy débil o incorrecta."
            message.contains("email") -> "El formato del email no es válido."
            message.contains("network") || message.contains("timeout") -> "Revisa tu conexión a internet."
            message.contains("unauthorized") || message.contains("invalid") || message.contains("credentials")
                    || message.contains("401") || message.contains("404") || message.contains("wrong") ->
                "Correo o contraseña incorrectos."
            else -> "Algo salió mal. Verifica tus datos e intenta de nuevo."
        }
    }

    fun register(
        email: String,
        birth_day: String,
        password: String,
        onSuccess: () -> Unit
    ){
        if (password.length < 8 || !password.any { it.isDigit() } || !password.any { it.isUpperCase() }) {
            uiState = uiState.copy(error = "La contraseña debe tener al menos 8 caracteres, un número y una mayúscula.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            authRepository.register(email, birth_day, password)
                .onSuccess { user ->
                    Log.d("AuthViewModel", "Registro exitoso: ${user.username}")
                    uiState = uiState.copy(isLoading = false, user = user, isAuthenticated = true)
                    onSuccess()
                }
                .onFailure { error ->
                    Log.e("AuthViewModel", "Error en registro: ${error.message}")
                    uiState = uiState.copy(isLoading = false, error = getErrorMessage(error))
                }
        }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            authRepository.login(email, password)
                .onSuccess { user ->
                    Log.d("AuthViewModel", "Login exitoso: ${user.username}")
                    uiState = uiState.copy(
                        isLoading = false,
                        user = user,
                        isAuthenticated = true
                    )
                    onSuccess()
                }
                .onFailure { error ->
                    Log.e("AuthViewModel", "Error en login: ${error.message}")
                    uiState = uiState.copy(
                        isLoading = false,
                        error = getErrorMessage(error)
                    )
                }
        }
    }

    fun logout() {
        authRepository.logout()
        uiState = AuthUiState()
        Log.d("AuthViewModel", "Logout exitoso")
    }

    fun isAuthenticated(): Boolean{
        return authRepository.isAuthenticated()
    }

}