package com.lumina.app_daymood.data.api.dto

import com.google.gson.annotations.SerializedName


// Request: solo envía las respuestas { "answers": { "q1": 5, "q2": 3, ... } }
data class FormRequest(
    @SerializedName("answers") val answers: Map<String, Int>
)

data class FullAnswerDTO(
    @SerializedName("key")       val key: String,
    @SerializedName("pregunta")  val pregunta: String,
    @SerializedName("respuesta") val respuesta: Int
)

data class FormResponse(
    @SerializedName("full_answers") val fullAnswers: List<FullAnswerDTO> = emptyList()
)
