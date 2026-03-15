package com.lumina.app_daymood.data.repositories

import android.util.Log
import com.lumina.app_daymood.data.api.ApiService
import com.lumina.app_daymood.data.api.dto.UserRequest
import com.lumina.app_daymood.data.firebase.FireStoreDataSource
import com.lumina.app_daymood.data.firebase.FirebaseAuthDataSource
import com.lumina.app_daymood.domain.models.UserModel
import com.lumina.app_daymood.domain.repositories.IAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val firestoreDataSource: FireStoreDataSource,
    private val apiService: ApiService
) : IAuthRepository {

    override suspend fun register(
        email: String,
        birth_day: String,
        password: String,
    ): Result<UserModel> = withContext(Dispatchers.IO) {
        try {
            val firebaseUser = firebaseAuthDataSource.createUser(email, password)
            val uid = firebaseUser.uid
            val username = generateRandomUsername()

            firebaseAuthDataSource.updateProfile(username)

            firestoreDataSource.saveUser(
                firebase_uid = uid,
                username = username,
                email = email,
                birth_day = birth_day
            )

            try {
                val token = firebaseAuthDataSource.getIdToken(false)
                sendToApi(token, uid, username, email, birth_day)
            } catch (e: Exception) {
                Log.w("AuthRepository", "API no disponible o falló: ${e.message}")
            }

            val user = UserModel(
                id = uid,
                firebase_uid = uid,
                username = username,
                email = email,
                birth_day = birth_day,
                start_date = System.currentTimeMillis()
            )

            Log.d("AuthRepository", "Usuario registrado exitosamente: $username")
            Result.success(user)

        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en registro: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<UserModel> = withContext(Dispatchers.IO) {
        try {
            val firebaseUser = firebaseAuthDataSource.signInUser(email, password)
            val user = firestoreDataSource.getUser(firebaseUser.uid)
            Log.d("AuthRepository", "Login exitoso: ${user?.username}")
            Result.success(user!!)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en login: ${e.message}")
            Result.failure(e)
        }
    }

    override fun logout() {
        firebaseAuthDataSource.logOut()
    }

    override fun isAuthenticated(): Boolean {
        return firebaseAuthDataSource.isAuthenticated()
    }

    override fun getCurrentUser(): String? {
        return firebaseAuthDataSource.getCurrentUser()
    }

    override suspend fun getIdToken(): String? {
        return try {
            firebaseAuthDataSource.getIdToken(false)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error obteniendo token: ${e.message}")
            null
        }
    }

    override suspend fun loadCurrentUser(): Result<UserModel> = withContext(Dispatchers.IO) {
        try {
            val uid = firebaseAuthDataSource.getCurrentUser()
                ?: return@withContext Result.failure(Exception("No hay sesión activa"))
            val user = firestoreDataSource.getUser(uid)
                ?: return@withContext Result.failure(Exception("Usuario no encontrado en Firestore"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateRandomUsername(): String {
        val chars = "abcdefghijklmnopqrstuvxyz1234567890"
        val randomString = (1..8).map { chars.random() }.joinToString("")
        return "user_$randomString"
    }

    private suspend fun sendToApi(
        token: String,
        firebaseUid: String,
        username: String,
        email: String,
        birthDay: String
    ) {
        val request = UserRequest(
            firebase_uid = firebaseUid,
            username = username,
            email = email,
            birth_day = birthDay
        )
        // aquí sí pasamos el Bearer manualmente porque es una llamada de "one-time" durante el registro
        apiService.registerUser(request) 
    }
}
