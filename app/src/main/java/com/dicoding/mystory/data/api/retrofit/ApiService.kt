package com.dicoding.mystory.data.api.retrofit

import com.dicoding.mystory.data.api.respone.AddNewStoryResponse
import com.dicoding.mystory.data.api.respone.LoginResponse
import com.dicoding.mystory.data.api.respone.RegisterResponse
import com.dicoding.mystory.data.api.respone.StoryResponse
import com.dicoding.mystory.data.api.respone.DetailStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(): StoryResponse

    @GET("stories/{id}")
    suspend fun getStoryDetail(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Response<DetailStoryResponse>

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null,
        @Header("Authorization") token: String
    ): Response<AddNewStoryResponse>
    }
