package com.lumina.app_daymood.data.api

import com.lumina.app_daymood.data.api.dto.CommentRequest
import com.lumina.app_daymood.data.api.dto.CommentsResponse
import com.lumina.app_daymood.data.api.dto.CreateEmotionRequest
import com.lumina.app_daymood.data.api.dto.CreateEmotionResponse
import com.lumina.app_daymood.data.api.dto.CreateRecordRequest
import com.lumina.app_daymood.data.api.dto.EmotionsResponse
import com.lumina.app_daymood.data.api.dto.FavoriteActionResponse
import com.lumina.app_daymood.data.api.dto.FavoriteRequest
import com.lumina.app_daymood.data.api.dto.FavoritesResponse
import com.lumina.app_daymood.data.api.dto.HabitsResponse
import com.lumina.app_daymood.data.api.dto.PostRequest
import com.lumina.app_daymood.data.api.dto.PostResponse
import com.lumina.app_daymood.data.api.dto.PostsResponse
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
    @POST("auth/register")
    suspend fun registerUser(
        @Header("Authorization") token: String,
        @Body request: UserRequest
    ): UserResponse

    @POST  ("auth/login")
    suspend fun loginUser(@Body request: UserRequest): UserResponse

    @GET("emotions")
    suspend fun getEmotions(
        @Header("Authorization") token: String
    ): EmotionsResponse

    @POST("emotions")
    suspend fun createEmotion(
        @Header("Authorization") token: String,
        @Body request: CreateEmotionRequest
    ): CreateEmotionResponse

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

    @GET("favorites")
    suspend fun getFavorites(
        @Header("Authorization") token: String
    ): FavoritesResponse

    @POST("favorites")
    suspend fun addFavorite(
        @Header("Authorization") token: String,
        @Body request: FavoriteRequest
    ): FavoriteActionResponse

    @POST("posts")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @Body request: PostRequest
    ): PostResponse

    @GET("posts")
    suspend fun getAllPosts(
        @Header("Authorization") token: String
    ): PostsResponse

    @GET ("posts/{postId}")
    suspend fun getPost(
        @Header("Authorization") token: String,
        @Path("postId") postId: String
    ): PostResponse

    @POST("/posts/comments")
    suspend fun addComment(
        @Header("Authorization") token: String,
        @Body request: CommentRequest
    ): CommentsResponse

    @GET("posts/comments")
    suspend fun getComments(
        @Header("Authorization") token: String,
        @Query("postId") postId: String
    ): CommentsResponse
}

 object RetrofitClient {
     private const val BASE_URL = "http://10.0.2.2:3000/"
     val instance: ApiService by lazy {
         Retrofit.Builder()
             .baseUrl(BASE_URL)
             .addConverterFactory(GsonConverterFactory.create())
             .build()
             .create(ApiService::class.java)
     }
 }