package com.lumina.app_daymood.data.repositories

import com.google.firebase.storage.FirebaseStorage
import com.lumina.app_daymood.data.api.ApiService
import com.lumina.app_daymood.data.api.dto.CreateRecordRequest
import com.lumina.app_daymood.data.api.dto.HabitsResponse
import com.lumina.app_daymood.data.api.dto.EmotionsResponse
import com.lumina.app_daymood.data.firebase.FirebaseAuthDataSource
import com.lumina.app_daymood.domain.models.RecordModel
import com.lumina.app_daymood.domain.repositories.IRecordRepository
import com.lumina.app_daymood.domain.models.EmotionModel as Emotion
import com.lumina.app_daymood.domain.models.HabitModel as Habit

class RecordRepositoryIml(
    private val apiService: ApiService,
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    storage: FirebaseStorage,
) : IRecordRepository {

    override suspend fun getEmotions(): Result<List<Emotion>> {
        return try {
            val token = firebaseAuthDataSource.getIdToken()
            val response = apiService.getEmotions("Bearer $token")
            Result.success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHabits(): Result<List<Habit>> {
        return try {
            val token = firebaseAuthDataSource.getIdToken()
            val response = apiService.getHabits("Bearer $token")
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
            val token = firebaseAuthDataSource.getIdToken()
            val request = CreateRecordRequest(date, note, emotionId, habitIds)
            val response = apiService.createRecord("Bearer $token", request)
            if (!response.success) throw Exception(response.message)
            val userId = firebaseAuthDataSource.getCurrentUser() ?: throw Exception("Usuario no autenticado")
            val record = response.data?.toDomain(userId) ?: throw Exception("No data en respuesta")
            Result.success(record)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecordByDate(userId: String?, date: String): Result<RecordModel?> {
        return try {
            val token = firebaseAuthDataSource.getIdToken()
            val response = apiService.getRecordByDate("Bearer $token", date)
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
            val token = firebaseAuthDataSource.getIdToken()
            val response = apiService.getRecordsByMonth("Bearer $token", year, month)
            val currentUserId = userId ?: firebaseAuthDataSource.getCurrentUser() ?: ""
            Result.success(response.data.map { it.toDomain(currentUserId) })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateRecord(
        recordId: String,
        emotionId: String,
        habitIds: List<String>,
        note: String?
    ): Result<RecordModel> {
        return try {
            val token = firebaseAuthDataSource.getIdToken()
            val request = CreateRecordRequest(
                date = "", // Backend must handle this
                note = note,
                emotionId = emotionId,
                habitIds = habitIds
            )
            val response = apiService.updateRecord("Bearer $token", recordId, request)
            if (!response.success) throw Exception(response.message)
            val currentUserId = firebaseAuthDataSource.getCurrentUser() ?: throw Exception("Usuario no autenticado")
            val record = response.data?.toDomain(currentUserId) ?: throw Exception("No data en respuesta")
            Result.success(record)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}