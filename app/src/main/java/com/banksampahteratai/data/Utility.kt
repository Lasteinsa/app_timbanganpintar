package com.banksampahteratai.data

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.banksampahteratai.R
import com.banksampahteratai.data.api.ApiConfig
import com.banksampahteratai.data.api.ResponseSessionAuth
import com.banksampahteratai.ui.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Utility {
    fun checkAuth(preference: DataPreference, context: Context) {
        val currentToken = preference.getToken ?: "expired"
        val removeToken: (DataPreference) -> Unit = {
            it.apply {
                setPreferenceString(DataPreference.TOKEN, "")
                setPreferenceBoolean(DataPreference.STATE_KEY, false)
            }
        }
        val retrofitInstance = ApiConfig.getApiService().sessionCheck(currentToken)

        retrofitInstance.enqueue(object: Callback<ResponseSessionAuth> {
            override fun onResponse(
                call: Call<ResponseSessionAuth>,
                response: Response<ResponseSessionAuth>
            ) {
                if(response.code() == 401) {
                    AlertDialog.Builder(context).apply {
                        setTitle(context.getString(R.string.session_ended))
                        setMessage(context.getString(R.string.session_ended_please_login))
                        setCancelable(false)
                        setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                            removeToken(preference)
                            val intent = Intent(context, LoginActivity::class.java).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        })
                        create()
                        show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseSessionAuth>, t: Throwable) {
                removeToken(preference)
                val intent = Intent(context, LoginActivity::class.java).apply{
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                Toast.makeText(context, context.getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
            }
        })

        if(!preference.isLogin) {
            removeToken(preference)
            val intent = Intent(context, LoginActivity::class.java).apply{
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}