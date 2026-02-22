package com.lumina.app_daymood.domain.models

import com.google.firebase.Timestamp

class Record(
    val id: String,
    val userId: String,
    val date: String,
    val note: String? = null,
    val emotion: Emotion,
    val habits: List<Habit> = emptyList(),
) {

}