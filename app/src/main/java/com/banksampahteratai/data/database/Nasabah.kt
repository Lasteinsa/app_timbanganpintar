package com.banksampahteratai.data.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Nasabah(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "idNasabah")
    var idNasabah: Int = 0,

    @ColumnInfo(name = "name")
    var name: String? = null,

): Parcelable