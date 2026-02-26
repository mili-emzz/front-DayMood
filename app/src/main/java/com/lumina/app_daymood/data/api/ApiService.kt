package com.lumina.app_daymood.data.api

import com.lumina.app_daymood.data.api.dto.CreateRecordRequest
import com.lumina.app_daymood.data.api.dto.EmotionsResponse
import com.lumina.app_daymood.data.api.dto.HabitsResponse
import com.lumina.app_daymood.data.api.dto.RecordResponse
import com.lumina.app_daymood.data.api.dto.RecordsResponse
import com.lumina.app_daymood.data.api.dto.UserRequest
import com.lumina.app_daymood.data.api.dto.UserResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService{
    // cambiar rutas cuando este la api lista
    @POST  ("auth/register")
    suspend fun registerUser(@Body request: UserRequest): UserResponse

    @POST  ("auth/login")
    suspend fun loginUser(@Body request: UserRequest): UserResponse

    @GET("emotions")
    suspend fun getEmotions(
        @Header("Authorization") token: String
    ): EmotionsResponse

    @GET("habits")
    suspend fun getHabits(
        @Header ("Authorization") token: String
    ): HabitsResponse

    @POST ("records")
    suspend fun createRecord(
        @Header ("Authorization") token: String,
        @Body request: CreateRecordRequest
    ): RecordResponse

    @GET("records")
    suspend fun getRecordsByMonth(
        @Header("Authorization") token: String,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): RecordsResponse

    @PUT("records/{recordId}")
    suspend fun updateRecord(
        @Header("Authorization") token: String,
        @Path("recordId") recordId: String,
        @Body request: CreateRecordRequest
    ): RecordResponse
}

 object RetrofitClient {
     private const val BASE_URL = "https://tu-api.com/" // cambiar esto cuando haya api
     val instance: ApiService by lazy {
         Retrofit.Builder()
             .baseUrl(BASE_URL)
             .addConverterFactory(GsonConverterFactory.create())
             .build()
             .create(ApiService::class.java)
     }
 }