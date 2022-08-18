package com.banksampahteratai.ui.main

import androidx.lifecycle.ViewModel
import com.banksampahteratai.data.DataPreference

class MainViewModel(private val preference: DataPreference) : ViewModel() {
    fun failed() {
        preference.apply {
            setPreferenceString(DataPreference.TOKEN, "")
            setPreferenceBoolean(DataPreference.STATE_KEY, false)
        }
    }
}