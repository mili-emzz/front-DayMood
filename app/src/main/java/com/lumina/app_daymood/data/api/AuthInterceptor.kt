package com.lumina.app_daymood.data.api

import com.lumina.app_daymood.data.firebase.FirebaseAuthDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authDataSource: FirebaseAuthDataSource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // Obtenemos el token de forma síncrona usando runBlocking. firebase ya maneja su propio caché interno, así que esto es rápido
        val token = try {
            // se hace una vez la peticion s eguarda y se reutiliza
            runBlocking {
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
