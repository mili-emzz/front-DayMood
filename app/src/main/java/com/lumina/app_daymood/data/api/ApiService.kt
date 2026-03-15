package com.lumina.app_daymood.data.api

import com.google.firebase.auth.FirebaseAuth
import com.lumina.app_daymood.BuildConfig
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
import com.lumina.app_daymood.data.api.dto.UpdatePostRequest
import com.lumina.app_daymood.data.api.dto.UserLoginRequest
import com.lumina.app_daymood.data.api.dto.UserRequest
import com.lumina.app_daymood.data.api.dto.UserResponse
import com.lumina.app_daymood.data.api.dto.WeeklyStatsResponse
import com.lumina.app_daymood.data.firebase.FirebaseAuthDataSource
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // le quitamos el header a todas las peticiones porque ya lo tenemos en el interceptor
    @POST("users/register")
    suspend fun registerUser(@Body request: UserRequest): UserResponse

    @POST("users/login")
    suspend fun loginUser(@Body request: UserLoginRequest): UserResponse

    // ========== EMOTIONS / FAVORITES ====================
    @GET("emotions")
    suspend fun getEmotions(): EmotionsResponse

    @GET("emotions/explore")
    suspend fun getUploadedEmotions(): EmotionsResponse

    @GET("records/habits")
    suspend fun getHabits(): HabitsResponse

    @Multipart
    @POST("emotions")
    suspend fun createEmotion(
        @Part("name") name: RequestBody,
        @Part("id_category") categoryId: RequestBody,
        @Part("save_to_favorites") saveToFavorites: RequestBody,
        @Part image: MultipartBody.Part
    ): CreateEmotionResponse

    @GET("emotions/favorites")
    suspend fun getFavorites(): FavoritesResponse

    @POST("emotions/favorites/{id}")
    suspend fun addFavorite(
        @Path("id") emotionId: String, @Body request: FavoriteRequest
    ): FavoriteActionResponse

    // ========== RECORD ====================
    @POST("records")
    suspend fun createRecord(@Body request: CreateRecordRequest): RecordResponse

    @GET("records/month")
    suspend fun getRecordsByMonth(
        @Query("year") year: String, @Query("month") month: Int
    ): RecordMonthResponse

    @GET("records/day")
    suspend fun getRecordByDate(@Query("date") date: String): RecordResponse

    // ========== STATS ====================
    @GET("stats/weekly")
    suspend fun getWeeklyStats(): WeeklyStatsResponse

    // ========== FORMS ====================
    @POST("forms/submit")
    suspend fun submitForm(@Body request: FormRequest): FormResponse

    // ========== FORUMS ===================
    @GET("forums/category/{categoryId}")
    suspend fun getForumsByCategory(@Path("categoryId") categoryId: Int): List<ForumCategoryDetailDTO>

    @GET("forums/detail/{forumId}")
    suspend fun getForumDetail(
        @Path(
            "forumId"
        ) forumId: String
    ): ForumDTO

    // ========== POSTS ====================
    @POST("posts")
    suspend fun createPost(@Body request: PostRequest): PostDTO

    @PATCH("posts/{postId}")
    suspend fun updatePost(
        @Path("postId") postId: String, @Body request: UpdatePostRequest
    ): PostDTO

    @DELETE("posts/{postId}")
    suspend fun deletePost(@Path("postId") postId: String): MessageResponse

    @POST("comments")
    suspend fun addComment(@Body request: CommentRequest): CommentDTO

    @DELETE("comments/{commentId}")
    suspend fun deleteComment(@Path("commentId") commentId: String): MessageResponse
}

object RetrofitClient {
    private val BASE_URL = BuildConfig.API_BASE_URL

    // Usamos la misma instancia de AuthDataSource para el Interceptor
    private val authDataSource = FirebaseAuthDataSource(FirebaseAuth.getInstance())

    private val client =
        OkHttpClient.Builder().addInterceptor(AuthInterceptor(authDataSource)).build()

    val instance: ApiService by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiService::class.java)
    }
}
