package com.banksampahteratai.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.banksampahteratai.data.database.Nasabah
import com.banksampahteratai.data.database.NasabahDao
import com.banksampahteratai.data.database.NasabahRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class NasabahRepository(application: Application) {
    private val mNasabahDao: NasabahDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = NasabahRoomDatabase.getDatabase(application)
        mNasabahDao = db.nasabahDao()
    }

    fun getAllNasabah(): LiveData<List<Nasabah>> = mNasabahDao.getAllNasabah()

    fun insert(nasabah: Nasabah) {
        executorService.execute { mNasabahDao.insert(nasabah) }
    }
    fun delete(nasabah: Nasabah) {
        executorService.execute { mNasabahDao.delete(nasabah) }
    }
    fun update(nasabah: Nasabah) {
        executorService.execute { mNasabahDao.update(nasabah) }
    }
}