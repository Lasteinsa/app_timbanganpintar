package com.banksampahteratai.data

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
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
    private var dialog: Dialog? = null
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

    fun showLoading(context: Context, cancelable: Boolean) {
        dialog  = Dialog(context)
        dialog?.setContentView(R.layout.loading)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(cancelable)
        try {
            dialog?.show()
        } catch (e: Exception) {
            Log.d("loadingError", e.toString())
        }
    }

    fun hideLoading() {
        try {
            dialog?.dismiss()
        } catch (e: Exception) {
            Log.d("loadingError", e.toString())
        }
    }

    fun showDialog(context: Context, titleDialog: String, messageDialog: String, confirmMessage: String, cancelMessage: String?, cancelable: Boolean, doFunc: ()-> Unit) {
        AlertDialog.Builder(context).apply {
            setTitle(titleDialog)
            setMessage(messageDialog)
            setCancelable(false)
            setPositiveButton(confirmMessage, DialogInterface.OnClickListener { _, _ ->
                doFunc()
            })
            if(cancelable) {
                setNegativeButton(cancelMessage, DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                })
            }
            create()
            show()
        }
    }
}