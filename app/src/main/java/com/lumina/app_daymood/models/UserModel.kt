package com.lumina.app_daymood.models

import com.google.firebase.Timestamp

//"id" UUID PRIMARY KEY DEFAULT gen_random_uuid (),
//"firebase_uid" varchar(200) UNIQUE NOT NULL,
//"username" varchar(20) UNIQUE,
//"email" varchar(40) UNIQUE,
//"birth_day" date NOT NULL,
//"start_date" timestamp DEFAULT CURRENT_TIMESTAMP,
//"id_forum" UUID
data class UserModel(
    val firebase_uid: String, //token q se manda al backenc
    val username: String,
    val email: String,
    val birth_day: String,
    val start_date: Timestamp? = null,
//    val id_forum: String //  la logica de esto se pospone
) {
    fun toMap(): MutableMap<String, Any> {
        return mutableMapOf(
            "firebase_uid" to this.firebase_uid,
            "username" to this.username,
            "email" to this.email,
            "birth_day" to this.birth_day,
            "start_date" to (this.start_date ?: Timestamp.now())
            // id_forum to aja
        )

    }
}