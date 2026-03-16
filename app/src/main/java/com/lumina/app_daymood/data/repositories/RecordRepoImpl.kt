package com.lumina.app_daymood.data.repositories

import android.util.Log
import com.lumina.app_daymood.data.api.ApiService
import com.lumina.app_daymood.data.api.dto.CreateRecordRequest
import com.lumina.app_daymood.data.firebase.FirebaseAuthDataSource
import com.lumina.app_daymood.domain.models.RecordModel
import com.lumina.app_daymood.domain.repositories.IRecordRepository
import com.lumina.app_daymood.domain.models.EmotionModel as Emotion
import com.lumina.app_daymood.domain.models.HabitCategoryModel as HabitCategory

class RecordRepositoryIml(
    private val apiService: ApiService,
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
) : IRecordRepository {

    override suspend fun getEmotions(): Result<List<Emotion>> {
        return try {
            val response = apiService.getEmotions()
            Result.success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHabits(): Result<List<HabitCategory>> {
        return try {
            val response = apiService.getHabits()
            Log.d("RecordRepository", "Habit Categories fetched: ${response.data.size}")
            Result.success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createRecord(
        date: String,
        emotionId: String,
        habitIds: List<String>,
        note: String?
    ): Result<RecordModel> {
        return try {
            val request = CreateRecordRequest(date, note, emotionId, habitIds)
            
            Log.d("RecordRepository", "Enviando record a API para fecha: $date")
            val response = apiService.createRecord(request)
            
            if (!response.success) {
                Log.e("RecordRepository", "API Error: ${response.message}")
                throw Exception(response.message ?: "Error desconocido en la API")
            }

            // Mantenemos la obtención del UID local para el mapeo al dominio
            val userId = firebaseAuthDataSource.getCurrentUser() ?: ""
            
            val recordData = response.data ?: throw Exception("La API no devolvió los datos del registro")
            val record = recordData.toDomain(userId)
            
            Log.d("RecordRepository", "Record creado exitosamente")
            Result.success(record)
        } catch (e: Exception) {
            Log.e("RecordRepository", "Error en createRecord: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getRecordByDate(userId: String?, date: String): Result<RecordModel?> {
        return try {
            val response = apiService.getRecordByDate(date)
            val currentUserId = userId ?: firebaseAuthDataSource.getCurrentUser() ?: ""
            val record = response.data?.toDomain(currentUserId)
            Result.success(record)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecordsByMonth(
        userId: String?,
        year: String,
        month: Int
    ): Result<List<RecordModel>> {
        return try {
            val response = apiService.getRecordsByMonth(year, month)
            val currentUserId = userId ?: firebaseAuthDataSource.getCurrentUser() ?: ""
            Result.success(response.data.map { it.toDomain(currentUserId) })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
