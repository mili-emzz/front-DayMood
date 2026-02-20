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

class AuthRepository(
    private val auth: FirebaseAuth = Firebase.auth,
    private val firestore: FirebaseFirestore = Firebase.firestore
//    private val apiService: ApiService
) {

    fun generateRandomUsername(): String {
        val chars = "abcdefghijklmnopqrstuvxyz1234567890"
        val randomString = (1..8).map { chars.random() }.joinToString("")
        return "user_$randomString"
    }

    private fun stringToTimestamp(dateString: String): Timestamp? {
        return try {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = format.parse(dateString)
            date?.let { Timestamp(it) }
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error al convertir fecha: ${e.message}")
            null
        }
    }

    private suspend fun saveUserToFirestore(
        firebaseUid: String,
        username: String,
        email: String,
        birthDay: String,
    ) {
        val birthDayTimestamp =
            stringToTimestamp(birthDay) ?: throw Exception("Fecha de nacimiento inválida")

        val user = hashMapOf(
            "firebase_uid" to firebaseUid,
            "username" to username,
            "email" to email,
            "birth_day" to birthDayTimestamp,
            "start_date" to Timestamp.Companion.now()
        )

        firestore.collection("Users").document(firebaseUid).set(user).await()
    }

    private fun getToken(
        username: String, email: String, birthDay: String, onSuccess: () -> Unit
    ) {
        auth.currentUser?.getIdToken(true)?.addOnSuccessListener { result ->
            val token = result.token
            val firebaseUid = auth.currentUser?.uid ?: ""

            if (token != null) {
                // aca podria ir la llamada a la api. Todavia en construccion
                // sendToApi
                val user = UserModel(
                    id = null, // lo generará postgres
                    firebase_uid = firebaseUid,
                    username = username,
                    email = email,
                    birth_day = birthDay,
                    start_date = Timestamp.Companion.now()
                )

                Log.d("Registro", "Usuario: $username, $email")
            }
        }
    }


    suspend fun registerUser(
        email: String,
        password: String,
        birthDay: String
    ) {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await();
            val uid: authResult.user?.uid ?: throw Exception("No se pudo obtener el UID")
            val username = generateRandomUsername()

            auth.currentUser?.updateProfile(
                userProfileChangeRequest {
                    UserProfileChangeRequest.Builder.setDisplayName = username
                }
            )?.await()

            saveUserToFirestore(uid, username, email, birthDay)

            getToken(username, email, birthDay) {
                Log.d("Registro", "Usuario registrado exitosamente")
            }

            // sendToApi
            Result.access(Unit)
        } catch (e: Exception) {

        }
    }
}