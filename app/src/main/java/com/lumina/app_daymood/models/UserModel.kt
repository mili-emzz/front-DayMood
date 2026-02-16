package com.lumina.app_daymood.models

//"id" UUID PRIMARY KEY DEFAULT gen_random_uuid (),
//"firebase_uid" varchar(200) UNIQUE NOT NULL,
//"username" varchar(20) UNIQUE,
//"email" varchar(40) UNIQUE,
//"birth_day" date NOT NULL,
//"start_date" timestamp DEFAULT CURRENT_TIMESTAMP,
//"id_forum" UUID
data class UserModel(
    val firebase_uid: String,
    val username: String,
    val email: String,
    val birth_day: String,
    val start_date: String,
//    val id_forum: String
) {

}