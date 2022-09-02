package com.banksampahteratai.data.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class ResponseLogin(
	val error: Boolean? = null,
	val message: String? = null,
	val status: Int? = null,
	val token: String? = null
)

data class ResponseSessionAuth(
	val success: Boolean? = null,
	val error: Boolean? = null,
	val status: Int? = null,
	val message: String? = null
)

@Parcelize
data class ResultUser(
	val id: String?,
	val namaLengkap: String?
): Parcelable

data class ResponseSearchUsers(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class DataItem(

	@field:SerializedName("kelamin")
	val kelamin: String? = null,

	@field:SerializedName("notelp")
	val notelp: String? = null,

	@field:SerializedName("nik")
	val nik: String? = null,

	@field:SerializedName("is_verify")
	val isVerify: String? = null,

	@field:SerializedName("nama_lengkap")
	val namaLengkap: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("last_active")
	val lastActive: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("tgl_lahir")
	val tglLahir: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null
)

