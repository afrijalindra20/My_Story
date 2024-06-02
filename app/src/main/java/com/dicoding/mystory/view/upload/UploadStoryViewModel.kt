package com.dicoding.mystory.view.upload

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mystory.data.api.respone.AddNewStoryResponse
import com.dicoding.mystory.data.database.UploadStoryRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class UploadStoryViewModel(private val repository: UploadStoryRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _uploadResult = MutableLiveData<UploadResultState>()
    val uploadResult: LiveData<UploadResultState> = _uploadResult

    fun uploadStory(imageMultipart: MultipartBody.Part, description: RequestBody, token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.uploadStory(token, imageMultipart, description, null, null)
                if (response.isSuccessful) {
                    _isLoading.value = false
                    _uploadResult.value = UploadResultState.Success(response.body()?.message ?: "Success")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("UploadStoryViewModel", "Error: ${response.code()}, Body: $errorBody")
                    _uploadResult.value = UploadResultState.Error(errorBody ?: "Unknown error")
                }
            } catch (e: HttpException) {
                Log.e("UploadStoryViewModel", "HttpException: ${e.response()?.errorBody()?.string()}")
                _uploadResult.value = UploadResultState.Error(e.localizedMessage ?: "An unexpected error occurred")
            } catch (e: Exception) {
                Log.e("UploadStoryViewModel", "Exception: ${e.message}")
                _uploadResult.value = UploadResultState.Error(e.localizedMessage ?: "An unexpected error occurred")
            }
        }
    }
}