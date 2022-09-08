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

data class ResponseTransaksi(
	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

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

data class ResponseDataSampah(

	@field:SerializedName("data")
	val data: List<ItemSampah?>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class ItemSampah(

	@field:SerializedName("id_kategori")
	val idKategori: String? = null,

	@field:SerializedName("harga")
	val harga: String? = null,

	@field:SerializedName("jumlah")
	val jumlah: String? = null,

	@field:SerializedName("jenis")
	val jenis: String? = null,

	@field:SerializedName("kategori")
	val kategori: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("harga_pusat")
	val hargaPusat: String? = null
)

data class ResponseKategoriSampah(
	@field:SerializedName("data")
	val data: List<KategoriItem?>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class KategoriItem(
	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("created_at")
	val created_at: String? = null,
)