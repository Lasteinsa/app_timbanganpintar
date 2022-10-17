package com.banksampahteratai.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SampahModel(
    val id: String?,
    val idKategori: String?,
    val kategori: String?,
    val jenis: String?,
    val harga: Int?,
    val hargaPusat: Int?,
    val jumlah: Double?
) : Parcelable

@Parcelize
data class User(
    val id: String?,
    val namaLengkap: String?
): Parcelable

data class TransaksiModel(
    var id_nasabah: String? = null,
    var date: String? = null,
    var transaksi: ArrayList<TransaksiData> = arrayListOf()
)

@Parcelize
data class TransaksiData(
    var id_sampah: String? = null,
    var jumlah: String? = null
): Parcelable

@Parcelize
data class SampahShow(
    val jenisSampah: String,
    val jumlahSampah: Double,
    val hargaSampah: Int,
    val totalHarga: Double
): Parcelable

@Parcelize
data class KategoriSampahModel(
    var id: String? = null,
    var name: String? = null,
    var created_at: String? = null
): Parcelable