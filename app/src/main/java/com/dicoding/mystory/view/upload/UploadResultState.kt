package com.dicoding.mystory.view.upload

sealed class UploadResultState {
    data class Success(val message: String) : UploadResultState()
    data class Error(val error: String) : UploadResultState()
}