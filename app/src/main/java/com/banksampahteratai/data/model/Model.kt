package com.banksampahteratai.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SampahModel(
    val jenisSampah: String,
    val jumlahSampah: Int,
    val hargaSampah: Int,
    val hasilSampah: Int
) : Parcelable

data class TransaksiModel(
    val id_nasabah: Int,
    val date: String,
    val transaksi: Map<String, TransaksiData>
)

data class TransaksiData(
    val slot : Map<String, String>
)