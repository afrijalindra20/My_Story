package com.dicoding.mystory.data.api.respone

import com.google.gson.annotations.SerializedName

data class AddNewStoryResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
