package com.banksampahteratai.ui.login

import androidx.lifecycle.ViewModel
import com.banksampahteratai.data.DataPreference
import com.banksampahteratai.data.DataPreference.Companion.STATE_KEY
import com.banksampahteratai.data.DataPreference.Companion.TOKEN
import com.banksampahteratai.data.api.ResponseLogin

class LoginViewModel(private val preference: DataPreference) : ViewModel() {
    fun login(user: ResponseLogin) {
        preference.apply {
            setPreferenceString(TOKEN, user.token.toString())
            setPreferenceBoolean(STATE_KEY, true)
        }
    }
}