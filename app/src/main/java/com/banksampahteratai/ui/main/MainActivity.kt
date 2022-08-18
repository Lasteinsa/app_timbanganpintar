package com.banksampahteratai.ui.main

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.banksampahteratai.R
import com.banksampahteratai.data.DataPreference
import com.banksampahteratai.data.api.ApiConfig
import com.banksampahteratai.data.api.ResponseSessionAuth
import com.banksampahteratai.databinding.ActivityMainBinding
import com.banksampahteratai.ui.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preference: DataPreference
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preference      = DataPreference(this)
        mainViewModel   = MainViewModel(preference)
        checkAuth()
    }

    private fun checkAuth() {
        val currentToken = preference.getToken ?: "expired"
        val retrofitInstance = ApiConfig.getApiService().sessionCheck(currentToken)

        retrofitInstance.enqueue(object: Callback<ResponseSessionAuth> {
            override fun onResponse(
                call: Call<ResponseSessionAuth>,
                response: Response<ResponseSessionAuth>
            ) {
                if(response.code() == 401) {
                    AlertDialog.Builder(this@MainActivity).apply {
                        setTitle(getString(R.string.session_ended))
                        setMessage(getString(R.string.session_ended_please_login))
                        setCancelable(false)
                        setPositiveButton("OK", DialogInterface.OnClickListener({_,_ ->
                            mainViewModel.failed()
                            val intent = Intent(this@MainActivity, LoginActivity::class.java).apply{
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(intent)
                        }))
                        create()
                        show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseSessionAuth>, t: Throwable) {
                mainViewModel.failed()
                val intent = Intent(this@MainActivity, LoginActivity::class.java).apply{
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
                Toast.makeText(this@MainActivity, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
            }

        })

        if(!preference.isLogin) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }
    }
}