package com.dicoding.mystory.di

import android.content.Context
import com.dicoding.mystory.data.database.UserRepository
import com.dicoding.mystory.data.api.retrofit.ApiConfig
import com.dicoding.mystory.data.database.LoginRepository
import com.dicoding.mystory.data.database.RegisterRepository
import com.dicoding.mystory.data.database.StoryRepository
import com.dicoding.mystory.data.database.UploadStoryRepository
import com.dicoding.mystory.data.pref.UserPreference
import com.dicoding.mystory.view.main.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = if (user != null && user.token.isNotBlank()) {
            ApiConfig.getApiService(user.token)
        } else {
            ApiConfig.getApiService("")
        }
        return StoryRepository.getInstance(apiService)
    }

    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }

    fun provideRegisterRepository(context: Context): RegisterRepository {
        val apiService = ApiConfig.getApiService("")
        return RegisterRepository(apiService)
    }

    fun provideLoginRepository(context: Context): LoginRepository {
        val apiService = ApiConfig.getApiService("")
        return LoginRepository(apiService)
    }

    fun provideUploadStoryRepository(context: Context): UploadStoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().firstOrNull() }
        val apiService = if (user != null && user.token.isNotBlank()) {
            ApiConfig.getApiService(user.token)
        } else {
            ApiConfig.getApiService("")
        }
        return UploadStoryRepository(apiService)
    }

}