package com.banksampahteratai.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.banksampahteratai.data.database.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SmartRepository(application: Application) {
    private val mNasabahDao: NasabahDao
    private val mDataSampahDao: DataSampahDao
    private val mKategoriSampah: KategoriSampahDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = SmartRoomDatabase.getDatabase(application)
        mNasabahDao     = db.nasabahDao()
        mDataSampahDao  = db.dataSampahDao()
        mKategoriSampah = db.kategoriSampahDao()
    }

    fun getAllNasabah(): LiveData<List<Nasabah>> = mNasabahDao.getAllNasabah()
    fun deleteAllNasabah() {
        executorService.execute {  mNasabahDao.deleteAllNasabah() }
    }
    fun searchNasabah(query: String): LiveData<List<Nasabah>> {
        return mNasabahDao.searchNasabah(query)
    }
    fun insertNasabah(nasabah: Nasabah) {
        executorService.execute { mNasabahDao.insertNasabah(nasabah) }
    }

    fun getAllDataSampah(): LiveData<List<DataSampah>> = mDataSampahDao.getAllDataSampah()
    fun deleteAllDataSampah() {
        executorService.execute {  mDataSampahDao.deleteAllDataSampah() }
    }
    fun insertDataSampah(dataSampah: DataSampah) {
        executorService.execute { mDataSampahDao.insertAllDataSampah(dataSampah) }
    }

    fun getAllKategoriSampah(): LiveData<List<KategoriSampah>> = mKategoriSampah.getAllKategoriSampah()
    fun deleteAllKategoriSampah() {
        executorService.execute { mKategoriSampah.deleteAllKategoriSampah() }
    }
    fun inserKategoriSampah(kategoriSampah: KategoriSampah) {
        executorService.execute { mKategoriSampah.insertKategoriSampah(kategoriSampah) }
    }
}