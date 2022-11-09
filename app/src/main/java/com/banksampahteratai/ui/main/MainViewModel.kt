package com.banksampahteratai.ui.main

import android.app.Application
import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.banksampahteratai.data.DataPreference
import com.banksampahteratai.data.database.Nasabah
import com.banksampahteratai.data.repository.NasabahRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application, private val preference: DataPreference) : ViewModel() {
    private val mNasabahRepository: NasabahRepository = NasabahRepository(application)

    fun getAllNasabah(): LiveData<List<Nasabah>> = mNasabahRepository.getAllNasabah()
    fun deleteAllNasabah() = mNasabahRepository.deleteAllNasabah()
    fun search(query: String): LiveData<List<Nasabah>> {
        return mNasabahRepository.searchNasabah(query)
    }

    fun insert(nasabah: Nasabah) {
        mNasabahRepository.insert(nasabah)
    }
    fun update(nasabah: Nasabah) {
        mNasabahRepository.update(nasabah)
    }
    fun delete(nasabah: Nasabah) {
        mNasabahRepository.delete(nasabah)
    }
}