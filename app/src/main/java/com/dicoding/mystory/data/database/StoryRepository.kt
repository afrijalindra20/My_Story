package com.dicoding.mystory.data.database

import com.dicoding.mystory.data.api.respone.StoryResponse
import com.dicoding.mystory.data.api.retrofit.ApiService
import com.dicoding.mystory.data.api.respone.DetailStoryResponse
import retrofit2.Response

class StoryRepository(private val apiService: ApiService) {
    suspend fun getStories(): StoryResponse {
        return apiService.getStories()
    }
    suspend fun getStoryDetail(id: String, token: String): Response<DetailStoryResponse> {
        return apiService.getStoryDetail(id, "Bearer $token")
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryRepository? = null

        fun getInstance(apiService: ApiService): StoryRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = StoryRepository(apiService)
                INSTANCE = instance
                instance
            }
        }
    }
}