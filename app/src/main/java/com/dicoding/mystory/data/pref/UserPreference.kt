package com.dicoding.mystory.data.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean
)

class UserPreference(private val dataStore: DataStore<Preferences>) {
    suspend fun saveSession(email: String, token: String, isLogin: Boolean) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = email
            preferences[TOKEN_KEY] = token
            preferences[IS_LOGIN_KEY] = isLogin
        }
    }

    fun getSession(): Flow<UserModel?> {
        return dataStore.data.map { preferences ->
            if (preferences[EMAIL_KEY]?.isNotBlank() == true &&
                preferences[TOKEN_KEY]?.isNotBlank() == true &&
                preferences[IS_LOGIN_KEY] != null
            ) {
                UserModel(
                    preferences[EMAIL_KEY] ?: "",
                    preferences[TOKEN_KEY] ?: "",
                    preferences[IS_LOGIN_KEY] ?: false
                )
            } else {
                null
            }
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(EMAIL_KEY)
            preferences.remove(IS_LOGIN_KEY)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}