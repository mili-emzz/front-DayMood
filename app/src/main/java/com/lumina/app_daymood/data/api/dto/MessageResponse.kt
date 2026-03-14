package com.lumina.app_daymood.data.api.dto

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("message") val message: String
)
