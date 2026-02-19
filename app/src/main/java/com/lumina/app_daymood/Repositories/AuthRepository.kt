package com.lumina.app_daymood.Repositories

import android.util.Log
import android.util.Log.e
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.lumina.app_daymood.models.UserModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AuthRepository(
    private val auth: FirebaseAuth = Firebase.auth,
    private val firestore: FirebaseFirestore = Firebase.firestore
) {
    var currentUser by mutableStateOf<UserModel?>(null)
    var showAlert by mutableStateOf(false)

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


    private fun saveUserToFirestore(
        username: String, email: String, birthDay: String, onSuccess: () -> Unit
    ) {
        val firebaseUid = auth.currentUser?.uid ?: ""
        val birthDayTimestamp = stringToTimestamp(birthDay)

        if (birthDayTimestamp == null) {
            Log.e("AuthViewModel", "Fecha de nacimiento inválida")
            showAlert = true
            return
        }

        val user = hashMapOf(
            "firebase_uid" to firebaseUid,
            "username" to username,
            "email" to email,
            "birth_day" to birthDayTimestamp,
            "start_date" to Timestamp.now()
        )

        firestore.collection("Users").document(firebaseUid).set(user).addOnSuccessListener {
            Log.d("AuthViewModel", "Usuario guardado en Firestore correctamente")

            // aquí se puede llamar a la API
            // sendToApi(username, email, birthDay)
            onSuccess()
        }.addOnFailureListener { exception ->
            Log.e("AuthViewModel", "Error al guardar en Firestore: ${exception.message}")
            showAlert = true
        }
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
                    start_date = Timestamp.now()
                )

                Log.d("Registro", "Usuario: $username, $email")
            }
        }
    }


    /*
    //en construccion...
    private fun sendToApi(token: String, email: String, username: String, birthDay: String) {
        val uid = auth.currentUser?.uid ?: ""

        val request = UserRequest(
            idToken = token,
            firebase_uid = uid,
            username = username,
            email = email,
            birthDay
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
               // aca va el retrofit
            }
        }
    }
*/


    fun getUserData(onSuccess: (UserModel?) -> Unit) {
        val user_id = auth.currentUser?.uid

        if (user_id != null) {
            firestore.collection("Users").document(user_id).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val userData = document.data
                    val user = userData?.let { UserModel.fromMap(it) }
                    currentUser = user
                } else {
                    currentUser = null
                    e("LoginViewModel", "Documento no existe en Firestore")
                }
            }.addOnFailureListener { exception ->
                currentUser = null
                e("LoginViewModel", "Error al obtener datos: ${exception.message}")
            }
        }
    }

}