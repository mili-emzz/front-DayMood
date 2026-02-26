package com.lumina.app_daymood.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.lumina.app_daymood.data.api.ApiService
import com.lumina.app_daymood.data.api.RetrofitClient
import com.lumina.app_daymood.data.firebase.FirebaseAuthDataSource
import com.lumina.app_daymood.data.firebase.FireStoreDataSource
import com.lumina.app_daymood.data.repositories.AuthRepositoryImpl
import com.lumina.app_daymood.data.repositories.RecordRepositoryIml
import com.lumina.app_daymood.domain.repositories.IAuthRepository
import com.lumina.app_daymood.domain.repositories.IRecordRepository
import com.lumina.app_daymood.presentation.viewmodels.AuthViewModel
import com.lumina.app_daymood.presentation.viewmodels.RecordViewModel

object AppModule {
    private val firebaseAuth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val firestore: FirebaseFirestore by lazy {
        Firebase.firestore
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
            apiService = null  // Cambia a apiService cuando lo tengas
        )
    }
    val recordRepository: IRecordRepository by lazy {
        RecordRepositoryIml(
            apiService = apiService,
            firebaseAuthDataSource = firebaseAuthDataSource
        )
    }

    // ViewModel Factory
    fun provideAuthViewModel(): AuthViewModel {
        return AuthViewModel(authRepository)
    }

    fun provideRecordViewModel(): RecordViewModel {
        return RecordViewModel(
            recordRepository = recordRepository,
            authRepository = authRepository
        )
    }

}