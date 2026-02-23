package com.lumina.app_daymood.domain.models

import com.google.firebase.Timestamp

class RecordModel(
    val id: String,
    val userId: String,
    val date: String,
    val note: String? = null,
    val emotion: EmotionModel,
    val habits: List<HabitModel> = emptyList(),
) {

}