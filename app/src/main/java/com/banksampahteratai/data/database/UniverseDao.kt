package com.banksampahteratai.data.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NasabahDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNasabah(nasabah: Nasabah)

    @Query("DELETE FROM Nasabah")
    fun deleteAllNasabah()

    @Query("SELECT * from nasabah ORDER BY id ASC")
    fun getAllNasabah(): LiveData<List<Nasabah>>

    @Query("SELECT * FROM nasabah WHERE name LIKE :query")
    fun searchNasabah(query: String): LiveData<List<Nasabah>>
}

@Dao
interface DataSampahDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllDataSampah(sampah: DataSampah)

    @Query("DELETE FROM DataSampah")
    fun deleteAllDataSampah()

    @Query("SELECT * from datasampah ORDER BY id ASC")
    fun getAllDataSampah(): LiveData<List<DataSampah>>
}

@Dao
interface KategoriSampahDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertKategoriSampah(sampah: KategoriSampah)

    @Query("DELETE FROM DataSampah")
    fun deleteAllKategoriSampah()

    @Query("SELECT * from kategorisampah ORDER BY id ASC")
    fun getAllKategoriSampah(): LiveData<List<KategoriSampah>>
}