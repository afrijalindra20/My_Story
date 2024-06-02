package com.dicoding.mystory.data.database

import com.dicoding.mystory.data.api.respone.AddNewStoryResponse
import com.dicoding.mystory.data.api.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class UploadStoryRepository(private val apiService: ApiService) {
    suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): Response<AddNewStoryResponse> {
        return apiService.uploadStory(file, description, lat, lon, token)
    }
}