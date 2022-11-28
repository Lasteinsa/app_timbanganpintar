package com.banksampahteratai.data.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "datasampah")
@Parcelize
data class DataSampah(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "idSampah")
    var idSampah: String?,

    @ColumnInfo(name = "idKategori")
    var idKategori: String?,

    @ColumnInfo(name = "kategori")
    var kategori: String?,

    @ColumnInfo(name = "jenisSampah")
    var jenisSampah: String?,

    @ColumnInfo(name = "jumlahSampah")
    var jumlahSampah: Double?,

    @ColumnInfo(name = "hargaSampah")
    var hargaSampah: Int?,

    @ColumnInfo(name = "hargaPusat")
    var hargaPusat: Int?,

    @ColumnInfo(name = "totalSampah")
    var totalHarga: Double?
): Parcelable