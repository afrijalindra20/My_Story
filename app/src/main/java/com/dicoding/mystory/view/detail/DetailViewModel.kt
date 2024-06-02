package com.dicoding.mystory.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mystory.data.api.respone.ListStoryItem
import com.dicoding.mystory.data.database.StoryRepository
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _story = MutableLiveData<ListStoryItem>()
    val story: LiveData<ListStoryItem> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getStoryDetail(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getStoryDetail(id, "Bearer <token>") // Ganti <token> dengan token yang sesuai
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.story?.let { _story.value = it }
                } else {
                    _error.value = response.message() // Menyimpan pesan error
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message ?: "Error occurred" // Menyimpan pesan error
            }
        }
    }
}