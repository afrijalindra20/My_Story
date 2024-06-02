package com.dicoding.mystory.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mystory.data.database.UserRepository
import com.dicoding.mystory.data.database.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: LoginRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    fun login(email: String, password: String, onSuccess: (token: String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                if (!response.error!!) {
                    val loginResult = response.loginResult
                    if (loginResult != null) {
                        val token = loginResult.token ?: ""
                        onSuccess(token)
                    }
                } else {

                }
            } catch (e: Exception) {
            }
        }
    }
}