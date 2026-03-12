package com.lumina.app_daymood.domain.repositories

import com.lumina.app_daymood.domain.models.UserModel
interface IAuthRepository{
    suspend fun register (email: String, birth_day: String, password: String): Result<UserModel>
    suspend fun login (email: String, password: String): Result<UserModel>
    fun logout()
    fun isAuthenticated(): Boolean
    fun getCurrentUser(): String?
    suspend fun getIdToken(): String?
    suspend fun loadCurrentUser(): Result<UserModel>
}