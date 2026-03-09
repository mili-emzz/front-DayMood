package com.lumina.app_daymood.data.api.dto

import com.lumina.app_daymood.domain.models.UserModel

data class UserRequest (
    val firebase_uid: String,
    val username: String,
    val email: String,
    val birth_day: String
)

data class UserResponse(
    val success: Boolean,
    val data: UserData? = null
)

data class UserData(
    val id: String?,
    val firebase_uid: String?,
    val username: String?,
    val email: String?,
    val birth_day: String?,
    val start_date: String?,
    val id_forum: String?
){
    fun toDomain(): UserModel = UserModel(
        id = id ?: "",
        firebase_uid = firebase_uid ?: "",
        username = username ?: "",
        email = email ?: "",
        birth_day = birth_day ?: "",
        start_date = 0L // Default value as API returns ISO string
    )
}