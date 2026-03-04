package com.lumina.app_daymood.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.app_daymood.domain.models.EmotionModel as Emotion
import com.lumina.app_daymood.domain.models.HabitModel as Habit
import com.lumina.app_daymood.domain.models.RecordModel as Record
import com.lumina.app_daymood.domain.repositories.IAuthRepository
import com.lumina.app_daymood.domain.repositories.IFavoritesRepository
import com.lumina.app_daymood.domain.repositories.IRecordRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

data class RecordUiState(
    // Catálogos
    val emotions: List<Emotion> = emptyList(),
    val habits: List<Habit> = emptyList(),
    val loadingCatalogs: Boolean = false,
    val selectedEmotionId: String? = null,
    val selectedNote: String? = null,
    // Record del día actual
    val currentRecord: Record? = null,
    // Records del mes (para el calendario)
    val monthRecords: List<Record> = emptyList(),

    // Operaciones
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

class RecordViewModel(
    private val recordRepository: IRecordRepository,
    private val favoritesRepository: IFavoritesRepository,
    private val authRepository: IAuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(RecordUiState())
        private set

    // Al crear el ViewModel se cargan catálogos inmediatamente
    init {
        loadCatalogs()
    }
    private fun loadCatalogs() {
        val userId = authRepository.getCurrentUser()

        viewModelScope.launch {
            val token = authRepository.getIdToken() ?: ""
            uiState = uiState.copy(loadingCatalogs = true)

            // Cargar emociones default
            val emotionsResult = recordRepository.getEmotions()
            val defaultEmotions = emotionsResult.getOrDefault(emptyList())

            // Cargar emociones favoritas del usuario
            val favoritesResult = favoritesRepository.getFavorites(token)
            val favoriteEmotions = favoritesResult.getOrDefault(emptyList())

            // Combinar listas y evitar duplicados
            val combinedEmotions = (defaultEmotions + favoriteEmotions).distinctBy { it.id }

            // Cargar hábitos
            val habitsResult = recordRepository.getHabits()

            uiState = uiState.copy(
                emotions = combinedEmotions,
                habits = habitsResult.getOrDefault(emptyList()),
                loadingCatalogs = false,
                error = emotionsResult.exceptionOrNull()?.message
                    ?: favoritesResult.exceptionOrNull()?.message
                    ?: habitsResult.exceptionOrNull()?.message
            )
        }
    }

    fun saveEmotionSelection(emotionId: String, note: String?) {
        uiState = uiState.copy(
            selectedEmotionId = emotionId,
            selectedNote = note
        )
    }

    fun saveRecord(
        date: String,
        habitIds: List<String>
    ) {
        val emotionId = uiState.selectedEmotionId
        if (emotionId == null) {
            uiState = uiState.copy(error = "Seleccioná una emoción primero")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null, saveSuccess = false)

            recordRepository.createRecord(
                date = date,
                emotionId = emotionId,
                habitIds = habitIds,
                note = uiState.selectedNote
            ).onSuccess { record ->
                uiState = uiState.copy(
                    isLoading = false,
                    saveSuccess = true,
                    currentRecord = record,
                    // Limpiar selección temporal
                    selectedEmotionId = null,
                    selectedNote = null
                )
            }.onFailure { error ->
                uiState = uiState.copy(
                    isLoading = false,
                    error = error.message ?: "Error al guardar"
                )
            }
        }
    }


    fun loadRecordByDate(date: String) {
        val userId = authRepository.getCurrentUser()

        viewModelScope.launch {
            recordRepository.getRecordByDate(userId, date)
                .onSuccess { record ->
                    uiState = uiState.copy(currentRecord = record)
                }
                .onFailure { error ->
                    uiState = uiState.copy(error = error.message)
                }
        }
    }

    fun loadRecordsByMonth(year: Int, month: Int) {
        val userId = authRepository.getCurrentUser()

        viewModelScope.launch {
            recordRepository.getRecordsByMonth(userId, year, month)
                .onSuccess { records ->
                    uiState = uiState.copy(monthRecords = records)
                }
                .onFailure { error ->
                    uiState = uiState.copy(error = error.message)
                }
        }
    }

    fun updateRecord(recordId: String, habitIds: List<String>) {
        val emotionId = uiState.selectedEmotionId
            ?: uiState.currentRecord?.emotion?.id
            ?: return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            recordRepository.updateRecord(
                recordId = recordId,
                emotionId = emotionId,
                habitIds = habitIds,
                note = uiState.selectedNote ?: uiState.currentRecord?.note
            ).onSuccess { record ->
                uiState = uiState.copy(
                    isLoading = false,
                    saveSuccess = true,
                    currentRecord = record,
                    selectedEmotionId = null,
                    selectedNote = null
                )
            }.onFailure { error ->
                uiState = uiState.copy(isLoading = false, error = error.message)
            }
        }
    }

    fun clearSuccess() {
        uiState = uiState.copy(saveSuccess = false)
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    // Saber si un día del calendario tiene record (para mostrar emoji)
    fun getEmotionForDate(date: String): Emotion? {
        return uiState.monthRecords.find { it.date == date }?.emotion
    }

    // Helper para formatear LocalDate al formato que usa la API
    fun formatDate(date: LocalDate): String {
        return "${date.year}-${date.monthValue.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}"
    }
}