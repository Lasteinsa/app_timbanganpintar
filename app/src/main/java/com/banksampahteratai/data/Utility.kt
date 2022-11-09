package com.banksampahteratai.data

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.banksampahteratai.R
import com.banksampahteratai.data.api.ApiConfig
import com.banksampahteratai.data.api.ResponseSessionAuth
import com.banksampahteratai.ui.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Utility {
    private var dialog: Dialog? = null
    private var iteration = 0
    fun checkAuth(preference: DataPreference, context: Context, view: View) {
        val currentToken = preference.getToken ?: "this program made by Einsa Kaslanov"
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
                           removeAllSession(preference, context)
                        })
                        create()
                        show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseSessionAuth>, t: Throwable) {
                showSnackbar(context, view, "Tidak Ada Internet. Sisa Percobaan ${5 - iteration}x", true)
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        iteration += 1
                        if(iteration < 5) {
                            checkAuth(preference, context, view)
                        } else {
                            iteration = 0
                            showSnackbar(context, view, context.getString(R.string.session_go_login), true)
                            Handler(Looper.getMainLooper()).postDelayed(
                                {
                                    removeAllSession(preference, context)
                                }, 5000
                            )
                        }
                    }, 5000
                )
            }
        })

        if(!preference.isLogin) {
            removeAllSession(preference, context)
        }
    }

    fun removeAllSession(preference: DataPreference, context: Context) {
        val removeToken: (DataPreference) -> Unit = {
            it.apply {
                setPreferenceString(DataPreference.TOKEN, "")
                setPreferenceBoolean(DataPreference.STATE_KEY, false)
            }
        }

        removeToken(preference)
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
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
            setCancelable(cancelable)
            setPositiveButton(confirmMessage, DialogInterface.OnClickListener { _, _ ->
                doFunc()
            })
            setNegativeButton(cancelMessage, DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            })
            create()
            show()
        }
    }

    fun showSnackbar(context: Context, view: View, text: String, isDanger: Boolean) {
        val snackbar: Snackbar  = Snackbar.make(view, text, Snackbar.LENGTH_LONG)
        val view                = snackbar.view
        val params              = view.layoutParams as FrameLayout.LayoutParams
        params.gravity          = Gravity.TOP
        view.layoutParams       = params
        snackbar.setTextColor(context.getColor(R.color.white))
        if(isDanger) {
            snackbar.view.background    = ResourcesCompat.getDrawable(context.resources, R.drawable.alert_danger, null)
        } else {
            snackbar.view.background    = ResourcesCompat.getDrawable(context.resources, R.drawable.alert_warning, null)
        }
        snackbar.show()
    }
}