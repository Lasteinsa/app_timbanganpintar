package com.banksampahteratai.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.banksampahteratai.data.DataPreference
import com.banksampahteratai.data.database.DataSampah
import com.banksampahteratai.data.database.KategoriSampah
import com.banksampahteratai.data.database.Nasabah
import com.banksampahteratai.data.repository.SmartRepository

class MainViewModel(application: Application, private val preference: DataPreference) : ViewModel() {
    private val mSmartRepository: SmartRepository = SmartRepository(application)

    fun getAllNasabah(): LiveData<List<Nasabah>> = mSmartRepository.getAllNasabah()
    fun deleteAllNasabah() = mSmartRepository.deleteAllNasabah()
    fun searchNasabah(query: String): LiveData<List<Nasabah>> {
        return mSmartRepository.searchNasabah(query)
    }
    fun insertNasabah(nasabah: Nasabah) {
        mSmartRepository.insertNasabah(nasabah)
    }

    fun deleteAllSampah() = mSmartRepository.deleteAllDataSampah()
    fun insertDataSampah(dataSampah: DataSampah) {
        mSmartRepository.insertDataSampah(dataSampah)
    }

    fun deleteAllKategoriSampah() = mSmartRepository.deleteAllKategoriSampah()
    fun insertKategoriSampah(kategoriSampah: KategoriSampah) {
        mSmartRepository.inserKategoriSampah(kategoriSampah)
    }
}