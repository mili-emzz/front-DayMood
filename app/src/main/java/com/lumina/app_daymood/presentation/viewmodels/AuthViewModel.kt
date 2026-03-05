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
        return when {
            error.message?.contains("password") == true ->
                "Contraseña incorrecta"
            error.message?.contains("email") == true ->
                "Email inválido o no registrado"
            error.message?.contains("network") == true ->
                "Error de conexión. Verifica tu internet"
            error.message?.contains("existe") == true ->
                "Este email ya está registrado"
            else ->
                error.message ?: "Error desconocido"
        }
    }

    fun register(
        email: String,
        birth_day: String,
        password: String,
        onSuccess: () -> Unit
    ){
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

    fun getUserdata(onSuccess: () -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            authRepository.getCurrentUser()
        }
    }

}