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