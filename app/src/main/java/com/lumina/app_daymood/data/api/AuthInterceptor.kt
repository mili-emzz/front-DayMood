package com.lumina.app_daymood.data.api

import com.lumina.app_daymood.data.firebase.FirebaseAuthDataSource
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authDataSource: FirebaseAuthDataSource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // Obtenemos el token usando runBlocking en Dispatchers.IO para evitar
        // deadlocks cuando los callbacks de Firebase se despachan en Main.
        // Firebase ya maneja su propio caché, así que esto es rápido.
        val token = try {
            runBlocking(Dispatchers.IO) {
                authDataSource.getIdToken(false)
            }
        } catch (e: Exception) {
            null
        }

        val requestBuilder = chain.request().newBuilder()

        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
