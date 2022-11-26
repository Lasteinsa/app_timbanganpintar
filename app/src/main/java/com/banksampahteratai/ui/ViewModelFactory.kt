package com.banksampahteratai.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.banksampahteratai.data.DataPreference
import com.banksampahteratai.ui.login.LoginViewModel
import com.banksampahteratai.ui.main.MainViewModel
import com.banksampahteratai.ui.main.ScaleViewModel

class ViewModelFactory private constructor(private val mApplication: Application, private val preference: DataPreference) : ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(application: Application, preference: DataPreference): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(application, preference)
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(mApplication, preference) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(preference) as T
            }
            modelClass.isAssignableFrom(ScaleViewModel::class.java) -> {
                ScaleViewModel(mApplication, preference) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}