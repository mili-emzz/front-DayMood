package com.lumina.app_daymood.data.api.dto

import com.google.gson.annotations.SerializedName
import com.lumina.app_daymood.domain.models.ForumModel

data class ForumDTO(
    @SerializedName("id")          val id: String,
    @SerializedName("min_age")     val min_age: Int,
    @SerializedName("max_age")     val max_age: Int,
    @SerializedName("id_category") val category_id: Int,
    @SerializedName("users")       val users: List<UserData>,
    @SerializedName("posts")       val posts: List<PostDTO>
) {
    fun toDomain(): ForumModel = ForumModel(
        id = id,
        min_age = min_age,
        max_age = max_age,
        id_category = category_id,
        users = users.map { it.toDomain() },
        posts = posts.map { it.toDomain() }
    )
}