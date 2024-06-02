package com.dicoding.mystory.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mystory.data.database.UserRepository
import com.dicoding.mystory.data.api.respone.ListStoryItem
import com.dicoding.mystory.data.database.StoryRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getStories() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val storyResponse = storyRepository.getStories()
                _stories.postValue(storyResponse.listStory)
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Error occurred")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}