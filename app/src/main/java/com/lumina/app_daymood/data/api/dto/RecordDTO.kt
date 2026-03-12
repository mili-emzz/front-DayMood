package com.lumina.app_daymood.data.api.dto

import com.google.gson.annotations.SerializedName
import com.lumina.app_daymood.domain.models.RecordModel

data class RecordDTO(
    @SerializedName("id")      val id: String,
    @SerializedName("date")    val date: String,
    @SerializedName("note")    val note: String? = null,
    @SerializedName("emotions") val emotion: EmotionDTO,   // API returns "emotions" object here
    @SerializedName("record_habits")  val recordHabits: List<RecordHabitDTO> = emptyList()
) {
    fun toDomain(userId: String): RecordModel = RecordModel(
        id = id,
        userId = userId,
        date = date,
        note = note,
        emotion = emotion.toDomain(),
        habits = recordHabits.map { it.habit.toDomain() }
    )
}

data class RecordHabitDTO(
    @SerializedName("id_record") val recordId: String?,
    @SerializedName("id_habit") val habitId: String?,
    @SerializedName("habits") val habit: HabitDTO
)

// Respuesta para un solo record (create, update, getByDate)
data class RecordResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val data: RecordDTO? = null
)

// Respuesta para lista de records (getByMonth)
data class RecordMonthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<RecordMonthDTO> = emptyList()
)

data class RecordMonthDTO(
    @SerializedName("id") val id: String,
    @SerializedName("date") val date: String,
    @SerializedName("emotions") val emotion: EmotionSimpleDTO
) {
    fun toDomain(userId: String): RecordModel = RecordModel(
        id = id,
        userId = userId,
        date = date,
        note = null,
        emotion = com.lumina.app_daymood.domain.models.EmotionModel(
            id = "", name = emotion.name, imgUrl = emotion.imgUrl, categoryId = 0
        ),
        habits = emptyList()
    )
}

data class EmotionSimpleDTO(
    @SerializedName("name") val name: String,
    @SerializedName("img_url") val imgUrl: String
)

data class CreateRecordRequest(
    @SerializedName("date")    val date: String,
    @SerializedName("note")    val note: String?,
    @SerializedName("id_emotion") val emotionId: String,
    @SerializedName("habits")  val habitIds: List<String>
)