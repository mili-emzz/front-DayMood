package com.lumina.app_daymood.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.app_daymood.domain.repositories.IAuthRepository
import com.lumina.app_daymood.domain.repositories.IStatsRepository
import com.lumina.app_daymood.presentation.views.stats.EmotionStat
import kotlinx.coroutines.launch

data class StatsUiState(
    val isLoading: Boolean = false,
    val stats: List<EmotionStat> = emptyList(),
    val error: String? = null
)

class StatsViewModel(
    private val statsRepository: IStatsRepository,
    private val authRepository: IAuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(StatsUiState())
        private set

    fun loadStats() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            val token = authRepository.getIdToken()
            if (token == null) {
                uiState = uiState.copy(isLoading = false, error = "No se pudo obtener el token")
                return@launch
            }

            statsRepository.getWeeklyStats(token)
                .onSuccess { stats ->
                    Log.d("StatsViewModel", "Stats cargadas: ${stats.size} emociones")
                    uiState = uiState.copy(isLoading = false, stats = stats)
                }
                .onFailure { error ->
                    Log.e("StatsViewModel", "Error cargando stats: ${error.message}")
                    uiState = uiState.copy(isLoading = false, error = error.message)
                }
        }
    }
}
