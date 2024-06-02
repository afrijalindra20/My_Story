package com.dicoding.mystory.data.database

import com.dicoding.mystory.data.pref.UserModel
import com.dicoding.mystory.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class UserRepository private constructor(
    private val userPreference: UserPreference
) {

    suspend fun saveSession(email: String, token: String, isLogin: Boolean) {
        userPreference.saveSession(email, token, isLogin)
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference)
            }.also { instance = it }
    }
}