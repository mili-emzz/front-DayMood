package com.lumina.app_daymood.domain.models

import com.google.firebase.Timestamp

class DailyRecord(
    val date: String = "",
    val emotionId: String = "",
    val emotionName: String = "",
    val emotionImgUrl: String = "",
    val habits: Map<String, Int> = emptyMap(),
    val note: String = "",
    val createdAt: Timestamp = Timestamp.now()
) {

}