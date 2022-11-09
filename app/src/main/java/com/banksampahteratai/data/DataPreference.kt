package com.banksampahteratai.data

import android.content.Context
import android.content.SharedPreferences

class DataPreference(context: Context) {
    private var prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
    private val editor = prefs.edit()

    fun setPreferenceString(prefKey: String, value: String) {
        editor.putString(prefKey, value)
        editor.apply()
    }

    fun setPreferenceBoolean(prefKey: String, value: Boolean) {
        editor.putBoolean(prefKey, value)
        editor.apply()
    }

    fun clearAllPreferences() {
        editor.clear().apply()
    }

    val getToken    = prefs.getString(TOKEN, "")
    val isLogin     = prefs.getBoolean(STATE_KEY, false)
    val firstTime   = prefs.getBoolean(FIRST_TIME, true)

    companion object {
        const val PREFERENCE    = "settings"
        const val TOKEN         = "token"
        const val STATE_KEY     = "state"
        const val FIRST_TIME    = "first"
    }
}