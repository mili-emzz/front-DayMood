package com.lumina.app_daymood.presentation.ViewModels

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

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore
    var currentUser by mutableStateOf<UserModel?>(null)
    var showAlert by mutableStateOf(false)

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onSuccess()
                        } else {
                            Log.d("Error en Firebase", "Error: ${task.exception?.localizedMessage}")
                            showAlert = true
                        }
                    }
            } catch (e: Exception) {
                Log.d("Error en Jetpack", "Error: ${e.localizedMessage}")
                showAlert = true
            }
        }
    }

    fun generateRandomUsername(): String {
        val chars = "abcdefghijklmnopqrstuvxyz1234567890"
        val randomString = (1..8)
            .map { chars.random() }
            .joinToString("")
        return "user_$randomString"
    }

    fun createUser(email: String, password: String, birth_day: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val username = generateRandomUsername()

                            val profileUpdates = userProfileChangeRequest {
                                displayName = username
                            }
                            auth.currentUser?.updateProfile(profileUpdates)
                                ?.addOnCompleteListener { profileTask ->
                                    if (profileTask.isSuccessful) {
                                        Log.d("LoginViewModel", "DisplayName actualizado")
                                    }
                                }
                            getToken(username, email, birth_day, onSuccess)
                        } else {
                            showAlert = true
                        }
                    }
            } catch (e: Exception) {
                Log.d("Error en Jetpack", "Error: ${e.localizedMessage}")
            }
        }
    }

    private fun getToken(
        username: String,
        email: String,
        birthDay: String,
        onSuccess: () -> Unit
    ) {
        auth.currentUser?.getIdToken(true)?.addOnSuccessListener { result ->
            val token = result.token
            val firebaseUid = auth.currentUser?.uid ?: ""

            if (token != null) {
                // aca podria ir la llamada a la api. Todavia en construccion
                // val request = UserRequest(token, email, username, birthDay)
                // apiService.registrar(request)...
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

//    //en construccion...
//    private fun sendToApi(token: String, email: String, username: String, birthDay: String) {
//        val uid = auth.currentUser?.uid ?: ""
//
//        val request = UserRequest(
//            idToken = token,
//            firebase_uid = uid,
//            username = username,
//            email = email,
//            birthDay
//        )
//
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//               // aca va el retrofit
//            }
//        }
//    }

    fun closeAlert() {
        showAlert = false
    }

    fun logout() {
        auth.signOut()
    }

    fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    fun getUserData(onSuccess: (UserModel?) -> Unit) {
        val user_id = auth.currentUser?.uid

        if (user_id != null) {
            firestore.collection("Users")
                .document(user_id)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userData = document.data
                        val user = userData?.let { UserModel.fromMap(it) }
                        currentUser = user
                    } else {
                        currentUser = null
                        e("LoginViewModel", "Documento no existe en Firestore")
                    }
                }
                .addOnFailureListener { exception ->
                    currentUser = null
                    e("LoginViewModel", "Error al obtener datos: ${exception.message}")
                }
        }
    }


}