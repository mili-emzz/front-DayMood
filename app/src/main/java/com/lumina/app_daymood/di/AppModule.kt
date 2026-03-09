package com.lumina.app_daymood.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import android.content.Context
import com.lumina.app_daymood.data.api.ApiService
import com.lumina.app_daymood.data.api.RetrofitClient
import com.lumina.app_daymood.data.firebase.FirebaseAuthDataSource
import com.lumina.app_daymood.data.firebase.FireStoreDataSource
import com.lumina.app_daymood.data.repositories.AuthRepositoryImpl
import com.lumina.app_daymood.data.repositories.EmotionRepositoryIml
import com.lumina.app_daymood.data.repositories.FavoritesRepositoryIml
import com.lumina.app_daymood.data.repositories.FormRepositoryImpl
import com.lumina.app_daymood.data.repositories.ForumRepositoryImpl
import com.lumina.app_daymood.data.repositories.RecordRepositoryIml
import com.lumina.app_daymood.domain.repositories.IAuthRepository
import com.lumina.app_daymood.domain.repositories.IEmotionRepository
import com.lumina.app_daymood.domain.repositories.IFavoritesRepository
import com.lumina.app_daymood.domain.repositories.IFormRepository
import com.lumina.app_daymood.domain.repositories.IForumRepository
import com.lumina.app_daymood.domain.repositories.IRecordRepository
import com.lumina.app_daymood.presentation.viewmodels.FavoritesViewModel
import com.lumina.app_daymood.presentation.viewmodels.AuthViewModel
import com.lumina.app_daymood.presentation.viewmodels.ForumViewModel
import com.lumina.app_daymood.presentation.viewmodels.FormViewModel
import com.lumina.app_daymood.presentation.viewmodels.RecordViewModel
import com.lumina.app_daymood.presentation.viewmodels.AddEmotionViewModel

object AppModule {
    // Context se inicializa una sola vez desde MainActivity
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val firebaseAuth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val firestore: FirebaseFirestore by lazy {
        val db = Firebase.firestore
        val settings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        db.firestoreSettings = settings
        db
    }
    private val firebaseAuthDataSource: FirebaseAuthDataSource by lazy {
        FirebaseAuthDataSource(firebaseAuth)
    }
    private val firestoreDataSource: FireStoreDataSource by lazy {
        FireStoreDataSource(firestore)
    }

    private val apiService: ApiService by lazy {
        RetrofitClient.instance
    }
    val authRepository: IAuthRepository by lazy {
        AuthRepositoryImpl(
            firebaseAuthDataSource = firebaseAuthDataSource,
            firestoreDataSource = firestoreDataSource,
            apiService = apiService
        )
    }
    val recordRepository: IRecordRepository by lazy {
        val storage = Firebase.storage
        RecordRepositoryIml(
            apiService = apiService,
            firebaseAuthDataSource = firebaseAuthDataSource,
            storage = storage
        )
    }

    val emotionRepository: IEmotionRepository by lazy {
        EmotionRepositoryIml(
            apiService = apiService,
            context = appContext          // para leer el Uri de la imagen seleccionada
        )
    }

    val favoritesRepository: IFavoritesRepository by lazy {
        FavoritesRepositoryIml(
            apiService = apiService
        )
    }

    val formRepository: IFormRepository by lazy {
        FormRepositoryImpl(
            apiService = apiService
        )
    }

    val forumRepository: IForumRepository by lazy {
        ForumRepositoryImpl(
            apiService = apiService
        )
    }

    // ViewModel Factory
    fun provideAuthViewModel(): AuthViewModel {
        return AuthViewModel(authRepository)
    }

    fun provideRecordViewModel(): RecordViewModel {
        return RecordViewModel(
            recordRepository = recordRepository,
            authRepository = authRepository,
            favoritesRepository = favoritesRepository
        )
    }

    fun provideAddEmotionViewModel(): AddEmotionViewModel {
        return AddEmotionViewModel(
            emotionRepository = emotionRepository,
            authRepository = authRepository
        )
    }

    fun provideFavoritesViewModel(): FavoritesViewModel {
        return FavoritesViewModel(
            favoritesRepository = favoritesRepository,
            authRepository = authRepository
        )
    }
    fun provideFormViewModel(): FormViewModel {
        return FormViewModel(
            formRepository = formRepository,
            authRepository = authRepository
        )
    }

    fun provideForumViewModel(): ForumViewModel {
        return ForumViewModel(
            forumRepository = forumRepository,
            authRepository = authRepository
        )
    }
}