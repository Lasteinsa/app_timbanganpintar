package com.banksampahteratai.data.api

data class ResponseLogin(
	val error: Boolean? = null,
	val message: String? = null,
	val status: Int? = null,
	val token: String? = null
)

