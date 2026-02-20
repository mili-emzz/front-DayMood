package com.lumina.app_daymood.data.api.dto

data class UserRequest(
    val idToken: String,
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
)