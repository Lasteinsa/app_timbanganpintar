package com.banksampahteratai.data.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NasabahDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(nasabah: Nasabah)

    @Update
    fun update(nasabah: Nasabah)

    @Delete
    fun delete(nasabah: Nasabah)

    @Query("SELECT * from nasabah ORDER BY id ASC")
    fun getAllNasabah(): LiveData<List<Nasabah>>
}