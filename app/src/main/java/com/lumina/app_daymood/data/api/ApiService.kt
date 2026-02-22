package com.lumina.app_daymood.data.api

import com.lumina.app_daymood.data.api.dto.UserRequest
import com.lumina.app_daymood.data.api.dto.UserResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService{
    @POST  ("auth/register")
    suspend fun registerUser(@Body request: UserRequest): UserResponse

    @POST  ("auth/login")
    suspend fun loginUser(@Body request: UserRequest): UserResponse
}

// RetrofitClient cuando este la API
// object RetrofitClient {
//     private const val BASE_URL = "https://tu-api.com/"
//
//     val instance: ApiService by lazy {
//         Retrofit.Builder()
//             .baseUrl(BASE_URL)
//             .addConverterFactory(GsonConverterFactory.create())
//             .build()
//             .create(ApiService::class.java)
//     }
// }