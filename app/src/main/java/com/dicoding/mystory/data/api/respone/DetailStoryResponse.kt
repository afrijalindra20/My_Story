package com.dicoding.mystory.data.api.respone

import com.dicoding.mystory.data.api.respone.ListStoryItem
import com.google.gson.annotations.SerializedName

data class DetailStoryResponse(
    @field:SerializedName("error") val error: Boolean,
    @field:SerializedName("message") val message: String,
    @field:SerializedName("story") val story: ListStoryItem
)