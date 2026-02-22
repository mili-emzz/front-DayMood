package com.lumina.app_daymood.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthDataSource(
    private val auth: FirebaseAuth
) {
    suspend fun createUser(email: String, birth_day: String, password: String): FirebaseUser {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()

        }
    }
}