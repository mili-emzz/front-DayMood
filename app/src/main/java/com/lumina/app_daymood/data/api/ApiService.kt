package com.lumina.app_daymood.data.api

import com.lumina.app_daymood.data.api.dto.CommentDTO
import com.lumina.app_daymood.data.api.dto.CommentRequest
import com.lumina.app_daymood.data.api.dto.CreateEmotionResponse
import com.lumina.app_daymood.data.api.dto.CreateRecordRequest
import com.lumina.app_daymood.data.api.dto.EmotionsResponse
import com.lumina.app_daymood.data.api.dto.FavoriteActionResponse
import com.lumina.app_daymood.data.api.dto.FavoriteRequest
import com.lumina.app_daymood.data.api.dto.FavoritesResponse
import com.lumina.app_daymood.data.api.dto.FormRequest
import com.lumina.app_daymood.data.api.dto.FormResponse
import com.lumina.app_daymood.data.api.dto.ForumCategoryDetailDTO
import com.lumina.app_daymood.data.api.dto.ForumDTO
import com.lumina.app_daymood.data.api.dto.HabitsResponse
import com.lumina.app_daymood.data.api.dto.MessageResponse
import com.lumina.app_daymood.data.api.dto.PostRequest
import com.lumina.app_daymood.data.api.dto.PostDTO
import com.lumina.app_daymood.data.api.dto.RecordMonthResponse
import com.lumina.app_daymood.data.api.dto.RecordResponse
import com.lumina.app_daymood.data.api.dto.UserRequest
import com.lumina.app_daymood.data.api.dto.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("users/register")
    suspend fun registerUser(
        @Header("Authorization") token: String,
        @Body request: UserRequest
    ): UserResponse

    @POST("users/login")
    suspend fun loginUser(
        @Header("Authorization") token: String,
        @Body request: UserRequest
    ): UserResponse

    // ========== EMOTIONS / FAVORITES ====================
    // -- EMOTIONS
    @GET("emotions")
    suspend fun getEmotions(
        @Header("Authorization") token: String
    ): EmotionsResponse

    @GET("emotions/explore")
    suspend fun getUploadedEmotions(
        @Header("Authorization") token: String
    ): EmotionsResponse

    @GET("habits")
    suspend fun getHabits(
        @Header("Authorization") token: String
    ): HabitsResponse

    @Multipart
    @POST("emotions")
    suspend fun createEmotion(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("id_category") categoryId: RequestBody,
        @Part("save_to_favorites") saveToFavorites: RequestBody,
        @Part image: MultipartBody.Part
    ): CreateEmotionResponse

    // -- EMOTIONs FAVORITES
    @GET("emotions/favorites")
    suspend fun getFavorites(
        @Header("Authorization") token: String
    ): FavoritesResponse

    @POST("emotions/favorites/{id}")
    suspend fun addFavorite(
        @Header("Authorization") token: String,
        @Path("id") emotionId: String,
        @Body request: FavoriteRequest
    ): FavoriteActionResponse

    // ========== RECORD ====================
    @POST("records")
    suspend fun createRecord(
        @Header("Authorization") token: String,
        @Body request: CreateRecordRequest
    ): RecordResponse

    // --RECORD BY MONTH
    @GET("records/month")
    suspend fun getRecordsByMonth(
        @Header("Authorization") token: String,
        @Query("year") year: String,
        @Query("month") month: Int
    ): RecordMonthResponse

    @GET("records/day")
    suspend fun getRecordByDate(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ): RecordResponse

    // ========== FORMS ====================
    @POST("forms/submit")
    suspend fun submitForm(
        @Header("Authorization") token: String,
        @Body request: FormRequest
    ): FormResponse

    // ========== FORUMS ===================
    // Paso 1: Obtener el foro por categoría (retorna lista, el backend filtra por edad)
    @GET("forums/category/{categoryId}")
    suspend fun getForumsByCategory(
        @Header("Authorization") token: String,
        @Path("categoryId") categoryId: Int
    ): List<ForumCategoryDetailDTO>

    // Paso 2: Obtener detalle del foro (posts + comentarios anidados)
    @GET("forums/detail/{forumId}")
    suspend fun getForumDetail(
        @Header("Authorization") token: String,
        @Path("forumId") forumId: String
    ): ForumDTO

    // ========== POSTS ====================
    @POST("posts")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @Body request: PostRequest
    ): PostDTO

    @PATCH("posts/{postId}")
    suspend fun updatePost(
        @Header("Authorization") token: String,
        @Path("postId") postId: String,
        @Body request: com.lumina.app_daymood.data.api.dto.UpdatePostRequest
    ): PostDTO

    @DELETE("posts/{postId}")
    suspend fun deletePost(
        @Header("Authorization") token: String,
        @Path("postId") postId: String
    ): MessageResponse // Assumes Response<Unit> or just doesn't crash on 204/200 OK

    @POST("comments")
    suspend fun addComment(
        @Header("Authorization") token: String,
        @Body request: CommentRequest
    ): CommentDTO

    @DELETE("comments/{commentId}")
    suspend fun deleteComment(
        @Header("Authorization") token: String,
        @Path("commentId") commentId: String
    ): MessageResponse
}

object RetrofitClient {
    private const val BASE_URL = "http://10.86.238.132:3000/api/"
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}