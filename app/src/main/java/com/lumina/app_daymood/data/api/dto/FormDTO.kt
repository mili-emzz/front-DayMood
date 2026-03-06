package com.lumina.app_daymood.data.api.dto

import com.google.gson.annotations.SerializedName


data class FormRequest(
    @SerializedName("answers")      val answers: Map<String, Int>,
    @SerializedName("full_answers")  val fullAnswers: List<FullAnswerDTO>
)

data class FullAnswerDTO(
    @SerializedName("key")       val key: String,
    @SerializedName("pregunta")  val pregunta: String,
    @SerializedName("respuesta") val respuesta: Int
)


data class FormResponse(
    @SerializedName("id")            val id: String,
    @SerializedName("id_user")       val idUser: String? = null,
    @SerializedName("date")          val date: String,
    @SerializedName("answers")       val answers: Map<String, Int>? = null,
    @SerializedName("full_answers")  val fullAnswers: List<FullAnswerDTO> = emptyList()
)
