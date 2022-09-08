package com.banksampahteratai.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SampahModel(
    val jenisSampah: String,
    val jumlahSampah: Int,
    val hargaSampah: Int,
    val hasilSampah: Int
) : Parcelable

@Parcelize
data class User(
    val id: String?,
    val namaLengkap: String?
): Parcelable

@Parcelize
data class KategoriSampah(
    val id: String? = null,
    val name: String? = null,
    val createdAt: String? = null
): Parcelable

data class TransaksiModel(
    var idNasabah: String? = null,
    var date: String? = null,
    var transaksi: ArrayList<TransaksiData> = arrayListOf()
)

data class TransaksiData(
    var slot: String? = null,
    var idSampah: String? = null,
    var jumlah: String? = null
)