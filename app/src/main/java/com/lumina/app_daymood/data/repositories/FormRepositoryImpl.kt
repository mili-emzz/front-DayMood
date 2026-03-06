package com.lumina.app_daymood.data.repositories

import com.lumina.app_daymood.data.api.ApiService
import com.lumina.app_daymood.data.api.dto.FormRequest
import com.lumina.app_daymood.data.api.dto.FullAnswerDTO
import com.lumina.app_daymood.domain.repositories.IFormRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FormRepositoryImpl(
    private val apiService: ApiService
) : IFormRepository {

    override suspend fun submitForm(
        token: String,
        answers: Map<String, Int>
    ): Result<List<FullAnswerDTO>> = withContext(Dispatchers.IO) {
        try {
            val request = FormRequest(answers = answers)
            val response = apiService.submitForm("Bearer $token", request)
            Result.success(response.fullAnswers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
