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
    suspend fun createUser(email: String, password: String): FirebaseUser {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user ?: throw Exception("Usuario no creado")
        } catch (e: Exception) {
            Log.e("FirebaseAuthDataSource", "Error al crear usuario: ${e.message}")
            throw e
        }
    }

    suspend fun updateProfile(email: String, password: String): FirebaseUser {
        return suspendCoroutine { continuation ->
            val profileUpdates = userProfileChangeRequest {
                this.displayName = displayName
            }

            auth.currentUser?.updateProfile(profileUpdates)
                ?.addOnSuccessListener {
                    Log.e("FirebaseAuthDataSource", "Perfil Actualizado")
                    continuation.resume(auth.currentUser!!)
                }
                ?.addOnFailureListener {
                    Log.e("FirebaseAuthDataSource", "Error al actualizar perfil: ${it.message}")
                    continuation.resumeWithException(it)
                }
                ?: continuation.resumeWithException(Exception("Usuario no autenticado"))
        }
    }

    suspend fun getIdToken(): String {
        return suspendCoroutine { continuation ->
            auth.currentUser?.getIdToken(true)
                ?.addOnSuccessListener { result ->
                    result.token?.let { token ->
                        Log.d("FirebaseAuthDataSource", "Token obtenido")
                        continuation.resume(token)
                    } ?: continuation.resumeWithException(Exception("Token null"))
                }
                ?.addOnFailureListener {
                    Log.e("FirebaseAuthDataSource", "Error al obtener token: ${it.message}")
                    continuation.resumeWithException(it)
                }
                ?: continuation.resumeWithException(Exception("Usuario no autenticado"))
        }
    }

    fun logOut() {
        auth.signOut()
    }

    fun isAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUser(): String? {
        return auth.currentUser?.uid
    }
}