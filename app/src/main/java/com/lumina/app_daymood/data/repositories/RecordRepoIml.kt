package com.lumina.app_daymood.data.repositories

import com.google.firebase.storage.FirebaseStorage
import com.lumina.app_daymood.data.api.ApiService
import com.lumina.app_daymood.data.firebase.FirebaseAuthDataSource
import com.lumina.app_daymood.domain.models.RecordModel
import com.lumina.app_daymood.domain.repositories.IRecordRepository
import com.lumina.app_daymood.domain.models.EmotionModel as Emotion // mi mejor descubrimiento
import com.lumina.app_daymood.domain.models.HabitModel as Habit

class RecordRepositoryIml(
    private val apiService: ApiService,
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val storage: FirebaseStorage
) : IRecordRepository {

    private val mockEmotions = listOf(
        Emotion(
            id = "em_happy",
            name = "Feliz",
            imgUrl = "https://via.placeholder.com/64/FFD700", // ejemplos
            categoryId = 1
        ),
        Emotion(
            id = "em_sad",
            name = "Triste",
            imgUrl = "https://via.placeholder.com/64/4169E1",
            categoryId = 2
        ),
        Emotion(
            id = "em_anxious",
            name = "Ansioso",
            imgUrl = "https://via.placeholder.com/64/FF4500",
            categoryId = 2
        ),
        Emotion(
            id = "em_calm",
            name = "Tranquilo",
            imgUrl = "https://via.placeholder.com/64/32CD32",
            categoryId = 1
        ),
        Emotion(
            id = "em_angry",
            name = "Enojado",
            imgUrl = "https://via.placeholder.com/64/DC143C",
            categoryId = 2
        ),
        Emotion(
            id = "em_excited",
            name = "Emocionado",
            imgUrl = "https://via.placeholder.com/64/FF69B4",
            categoryId = 1
        )
    )

    private val mockHabits = listOf(
        Habit(id = "hab_exercise", name = "Ejercicio", categoryId = 3),
        Habit(id = "hab_sleep", name = "Dormir bien", categoryId = 3),
        Habit(id = "hab_water", name = "Tomar agua", categoryId = 3),
        Habit(id = "hab_meditate", name = "Meditar", categoryId = 4),
        Habit(id = "hab_read", name = "Leer", categoryId = 4),
        Habit(id = "hab_social", name = "Socializar", categoryId = 5),
        Habit(id = "hab_work", name = "Trabajo/Estudio", categoryId = 6)
    )

    // Cache local de records (reemplaza Firestore del ViewModel viejo)
    private val recordsCache = mutableMapOf<String, RecordModel>()

    override suspend fun getEmotions(): Result<List<Emotion>> {
        return try {
            // val token = firebaseAuthDataSource.getIdToken()
            // val response = apiService!!.getEmotions("Bearer $token")
            // Result.success(response.data.map { it.toDomain() })

            Result.success(mockEmotions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHabits(): Result<List<Habit>> {
        return try {
            // val token = firebaseAuthDataSource.getIdToken()
            // val response = apiService!!.getHabits("Bearer $token")
            // Result.success(response.data.map { it.toDomain() })

            Result.success(mockHabits)
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
            // val token = firebaseAuthDataSource.getIdToken()
            // val request = CreateRecordRequest(date, note, emotionId, habitIds)
            // val response = apiService!!.createRecord("Bearer $token", request)
            // if (!response.success) throw Exception(response.message)
            // val userId = firebaseAuthDataSource.getCurrentUserId()!!
            // val record = response.data?.toDomain(userId) ?: throw Exception("No data en respuesta")
            // Result.success(record)

            val emotion = mockEmotions.find { it.id == emotionId }
                ?: return Result.failure(Exception("Emoción no encontrada"))
            val habits = mockHabits.filter { it.id in habitIds }
            val userId = firebaseAuthDataSource.getCurrentUser() ?: "mock_user"

            val record = RecordModel(
                id = "rec_${System.currentTimeMillis()}",
                userId = userId,
                date = date,
                note = note,
                emotion = emotion,
                habits = habits
            )
            recordsCache[date] = record
            Result.success(record)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecordByDate(userId: String?, date: String): Result<RecordModel?> {
        return try {
            // val token = firebaseAuthDataSource.getIdToken()
            // val response = apiService!!.getRecordByDate("Bearer $token", date)
            // val record = response.data?.toDomain(userId)
            // Result.success(record)

            Result.success(recordsCache[date])
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecordsByMonth(
        userId: String?,
        year: Int,
        month: Int
    ): Result<List<RecordModel>> {
        return try {
            // val token = firebaseAuthDataSource.getIdToken()
            // val response = apiService!!.getRecordsByMonth("Bearer $token", year, month)
            // Result.success(response.data.map { it.toDomain(userId) })

            val prefix = "$year-${month.toString().padStart(2, '0')}"
            val records = recordsCache.filterKeys { it.startsWith(prefix) }.values.toList()
            Result.success(records)
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
            // val token = firebaseAuthDataSource.getIdToken()
            // val request = CreateRecordRequest("", note, emotionId, habitIds)
            // val response = apiService!!.updateRecord("Bearer $token", recordId, request)
            // if (!response.success) throw Exception(response.message)
            // val userId = firebaseAuthDataSource.getCurrentUserId()!!
            // Result.success(response.data!!.toDomain(userId))

            val existing = recordsCache.values.find { it.id == recordId }
                ?: return Result.failure(Exception("Record no encontrado"))
            val emotion = mockEmotions.find { it.id == emotionId }
                ?: return Result.failure(Exception("Emoción no encontrada"))
            val habits = mockHabits.filter { it.id in habitIds }

            val updated = existing.copy(emotion = emotion, habits = habits, note = note)
            recordsCache[existing.date] = updated
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}