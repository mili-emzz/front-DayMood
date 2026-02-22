package com.lumina.app_daymood.domain.models

data class UserModel(
    val id: String?,
    val firebase_uid: String, //token q se manda al backenc
    val username: String,
    val email: String,
    val birth_day: String,
    val start_date: Long,
//  val id_forum: String //  la logica de esto se pospone
)