package com.banksampahteratai.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.banksampahteratai.data.DataPreference
import com.banksampahteratai.data.database.DataSampah
import com.banksampahteratai.data.database.KategoriSampah
import com.banksampahteratai.data.repository.SmartRepository

class ScaleViewModel(application: Application, private val preference: DataPreference) : ViewModel() {
    private val mSmartRepository: SmartRepository = SmartRepository(application)

    fun getAllDataSampah(): LiveData<List<DataSampah>> = mSmartRepository.getAllDataSampah()

    fun getAllKategoriSampah(): LiveData<List<KategoriSampah>> = mSmartRepository.getAllKategoriSampah()
}