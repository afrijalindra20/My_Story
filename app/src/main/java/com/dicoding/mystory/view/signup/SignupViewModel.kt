package com.dicoding.mystory.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mystory.data.database.RegisterRepository
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: RegisterRepository) : ViewModel() {
    private val _signupResult = MutableLiveData<Result<Unit>>()
    val signupResult: LiveData<Result<Unit>> = _signupResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _signupResult.value = Result.Loading
            try {
                val response = repository.register(name, email, password)
                if (!response.error!!) {
                    _signupResult.value = Result.Success(Unit)
                } else {
                    val errorMessage = response.message
                    if (errorMessage?.contains("Email is already taken", ignoreCase = true) == true) {
                        _signupResult.value = Result.Error("Email sudah terdaftar")
                    } else {
                        _signupResult.value = Result.Error("Gagal mendaftar: ${response.message}")
                    }
                }
            } catch (e: Exception) {
                _signupResult.value = Result.Error("Terjadi kesalahan: ${e.message}")
            }
        }
    }

    sealed class Result<out R> {
        data class Success<out T>(val data: T) : Result<T>()
        data class Error(val errorMessage: String) : Result<Nothing>()
        object Loading : Result<Nothing>()
    }
}