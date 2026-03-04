package com.lumina.app_daymood.domain.repositories

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.lumina.app_daymood.domain.models.UserModel
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

interface IAuthRepository{
    suspend fun register (email: String, birth_day: String, password: String): Result<UserModel>
    suspend fun login (email: String, password: String): Result<UserModel>
    fun logout()
    fun isAuthenticated(): Boolean
    fun getCurrentUser(): String?
    suspend fun getIdToken(): String?
}