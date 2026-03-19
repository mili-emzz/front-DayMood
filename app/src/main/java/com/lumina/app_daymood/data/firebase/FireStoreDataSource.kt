package com.lumina.app_daymood.data.firebase

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.lumina.app_daymood.domain.models.UserModel
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class FireStoreDataSource(
    private val firestore: FirebaseFirestore
) {

    private fun stringToTimestamp(dateString: String): Timestamp {
        return try {
            // Actualizado a yyyy-MM-dd para coincidir con la UI y la API
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = format.parse(dateString) ?: throw Exception("Fecha inválida")
            Timestamp(date)
        } catch (e: Exception) {
            Log.e("FirestoreDataSource", "Error al convertir fecha: ${e.message}")
            throw Exception("Formato de fecha inválido. Use yyyy-MM-dd")
        }
    }

    private fun timestampToString(timestamp: Timestamp): String {
        // Al recuperar de Firestore, mantenemos yyyy-MM-dd para consistencia interna
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return format.format(timestamp.toDate())
    }

    suspend fun saveUser(
        firebase_uid: String,
        username: String,
        email: String,
        birth_day: String
    ) {
        try {
            val birth_day_timestamp = stringToTimestamp(birth_day)

            val userData = hashMapOf(
                "firebase_uid" to firebase_uid,
                "username" to username,
                "email" to email,
                "birth_day" to birth_day_timestamp,
                "start_date" to Timestamp.now()
            )

            firestore.collection("users")
                .document(firebase_uid)
                .set(userData)
                .await()
            Log.d("FirestoreDataSource", "Usuario guardado en Firestore: $username")
        } catch (e: Exception) {
            Log.d("FirestoreDataSource", "Error al guardar usuario en Firestore")
            throw e
        }
    }

    suspend fun getUser(firebase_uid: String): UserModel? {
        return try {
            val document = firestore.collection("users")
                .document(firebase_uid)
                .get()
                .await()

            if (document.exists()) {
                val data = document.data!!
                UserModel(
                    id = document.id,
                    firebase_uid = data["firebase_uid"] as String,
                    username = data["username"] as String,
                    email = data["email"] as String,
                    birth_day = timestampToString(data["birth_day"] as Timestamp),
                    start_date = (data["start_date"] as Timestamp).seconds * 1000
                )
            } else {
                throw Exception("Usuario no ecnontrado en firestore")
            }
        } catch (e: Exception) {
            Log.d("FirestoreDataSource", "Error al obtener usuario de Firestore")
            throw e
        }
    }
}
