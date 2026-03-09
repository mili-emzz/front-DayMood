package com.lumina.app_daymood.data.api.dto

import com.google.gson.annotations.SerializedName

data class ForumCategoryDetailDTO(
    @SerializedName("id") val id: String,
    @SerializedName("min_age") val min_age: Int,
    @SerializedName("max_age") val max_age: Int,
    @SerializedName("id_category") val id_category: Int,
)

