package com.lumina.app_daymood.data.api

import com.lumina.app_daymood.data.api.dto.CommentRequest
import com.lumina.app_daymood.data.api.dto.CommentsResponse
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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("auth/register")
    suspend fun registerUser(
        @Header("Authorization") token: String,
        @Body request: UserRequest
    ): UserResponse

    @POST("auth/login")
    suspend fun loginUser(@Body request: UserRequest): UserResponse


    @GET("api/emotions")
    suspend fun getEmotions(
        @Header("Authorization") token: String
    ): EmotionsResponse

    @GET("api/emotions/explore")
    suspend fun getEmotionsExplore(
        @Header("Authorization") token: String
    ): EmotionsResponse

    /**
     * POST /api/emotions - crear emoción personalizada.
     * Se envía como multipart/form-data:
     *   name, id_category, save_to_favorites (RequestBody de texto) + image (MultipartBody.Part)
     */
    @Multipart
    @POST("api/emotions")
    suspend fun createEmotion(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("id_category") categoryId: RequestBody,
        @Part("save_to_favorites") saveToFavorites: RequestBody,
        @Part image: MultipartBody.Part
    ): CreateEmotionResponse

    @DELETE("api/emotions/{id}")
    suspend fun deleteEmotion(
        @Header("Authorization") token: String,
        @Path("id") emotionId: String
    ): FavoriteActionResponse

    @GET("api/emotions/favorites")
    suspend fun getFavorites(
        @Header("Authorization") token: String
    ): FavoritesResponse

    @POST("emotions/favorites/")
    suspend fun addFavorite(
        @Header("Authorization") token: String,
        @Body request: FavoriteRequest
    ): FavoriteActionResponse

    @DELETE("api/emotions/{id}")
    suspend fun deleteFavorite(
        @Header("Authorization") token: String,
        @Path("id") emotionId: String
    ): FavoriteActionResponse


    @GET("habits")
    suspend fun getHabits(
        @Header("Authorization") token: String
    ): HabitsResponse


    @POST("api/records")
    suspend fun createRecord(
        @Header("Authorization") token: String,
        @Body request: CreateRecordRequest
    ): RecordResponse

    @GET("records")
    suspend fun getRecordsByMonth(
        @Header("Authorization") token: String,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): RecordsResponse

    @GET("records/day")
    suspend fun getRecordByDate(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ): RecordResponse

    @PUT("records/{recordId}")
    suspend fun updateRecord(
        @Header("Authorization") token: String,
        @Path("recordId") recordId: String,
        @Body request: CreateRecordRequest
    ): RecordResponse


    @POST("posts")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @Body request: PostRequest
    ): PostResponse

    @GET("posts")
    suspend fun getAllPosts(
        @Header("Authorization") token: String
    ): PostsResponse

    @GET("posts/{postId}")
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