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
    val message: String,
    val data: UserData?
)

data class UserData(
    val id: String,
    val firebase_uid: String,
    val username: String,
    val email: String,
){
    fun toDomain(): UserModel = UserModel(
        id = id,
        firebase_uid = firebase_uid,
        username = username,
        email = email,
        birth_day = "", // Default value as API doesn't provide it here
        start_date = 0L // Default value as API doesn't provide it here
    )
}