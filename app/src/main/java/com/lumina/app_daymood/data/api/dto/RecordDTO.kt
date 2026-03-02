package com.lumina.app_daymood.data.api.dto

import com.google.gson.annotations.SerializedName
import com.lumina.app_daymood.domain.models.RecordModel
data class RecordDTO(
    @SerializedName("id")      val id: String,
    @SerializedName("date")    val date: String,
    @SerializedName("note")    val note: String? = null,
    @SerializedName("emotion") val emotion: EmotionDTO,
    @SerializedName("habits")  val habits: List<HabitDTO> = emptyList()
) {
    fun toDomain(userId: String): RecordModel = RecordModel(
        id = id,
        userId = userId,
        date = date,
        note = note,
        emotion = emotion.toDomain(),
        habits = habits.map { it.toDomain() }
    )
}

// Respuesta para un solo record (create, update, getByDate)
data class RecordResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String = "",
    @SerializedName("data")    val data: RecordDTO? = null
)

// Respuesta para lista de records (getByMonth)
data class RecordsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data")    val data: List<RecordDTO> = emptyList()
)

data class CreateRecordRequest(
    @SerializedName("date")       val date: String,
    @SerializedName("note")       val note: String?,
    @SerializedName("emotion_id") val emotionId: String,
    @SerializedName("habit_ids")  val habitIds: List<String>
)