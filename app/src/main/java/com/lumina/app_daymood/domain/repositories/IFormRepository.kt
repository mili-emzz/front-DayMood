package com.lumina.app_daymood.domain.repositories

import com.lumina.app_daymood.data.api.dto.FullAnswerDTO

interface IFormRepository {
    suspend fun submitForm(token: String, answers: Map<String, Int>): Result<List<FullAnswerDTO>>
}
