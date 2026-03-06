package com.lumina.app_daymood.domain.repositories

import com.lumina.app_daymood.domain.models.RecordModel
import com.lumina.app_daymood.domain.models.EmotionModel as Emotion
import com.lumina.app_daymood.domain.models.HabitModel as Habit
import com.lumina.app_daymood.domain.models.RecordModel as Record
interface IRecordRepository {
    suspend fun getEmotions(): Result<List<Emotion>>
    suspend fun getHabits(): Result<List<Habit>>
    suspend fun createRecord(
        date: String,
        emotionId: String,
        habitIds: List<String>,
        note: String?
    ): Result<Record>

    suspend fun getRecordByDate(userId: String?, date: String): Result<RecordModel?>  // null si no hay registro ese día

    suspend fun getRecordsByMonth(
        userId: String?,
        year: String,
        month: Int
    ): Result<List<RecordModel>>

}