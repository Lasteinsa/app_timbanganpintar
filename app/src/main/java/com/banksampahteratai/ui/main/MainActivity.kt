package com.banksampahteratai.ui.main

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.banksampahteratai.R
import com.banksampahteratai.data.DataPreference
import com.banksampahteratai.data.Utility
import com.banksampahteratai.data.api.*
import com.banksampahteratai.databinding.ActivityMainBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preference: DataPreference
    private lateinit var mainViewModel: MainViewModel
    private lateinit var utility: Utility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preference      = DataPreference(this)
        mainViewModel   = MainViewModel(preference)
        supportActionBar?.hide()

        utility = Utility()
        utility.checkAuth(preference, this@MainActivity)

        setupAction()
    }

    private fun alertInfo(isShow: Boolean) {
        val loginAlertShow =
            ObjectAnimator.ofFloat(binding.loginInfoAlert, "alpha", 1f).setDuration(500)
        val loginAlertHide =
            ObjectAnimator.ofFloat(binding.loginInfoAlert, "alpha", 0f).setDuration(500)

        if(isShow) {
            loginAlertShow.start()
        } else {
            loginAlertHide.start()
        }
    }

    private fun isLoading(load: Boolean) {
        if(load) {
            binding.loadingLogin.root.visibility = View.VISIBLE
            binding.loadingLogin.root.bringToFront()
        } else {
            binding.loadingLogin.root.visibility = View.INVISIBLE
        }
    }

    private fun setupAction() {
        binding.searchUser.setOnClickListener {
            binding.searchUser.onActionViewExpanded()
        }
        binding.searchUser.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                isLoading(true)
                searchUsers(p0)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })
    }

    private fun searchUsers(p0: String?) {
        val retrofitInstance = ApiConfig.getApiService().getNasabah(preference.getToken.toString(), p0)
        retrofitInstance.enqueue(object: Callback<ResponseSearchUsers> {
            override fun onResponse(
                call: Call<ResponseSearchUsers>,
                response: Response<ResponseSearchUsers>
            ) {
                if(response.isSuccessful) {
                    isLoading(false)
                    val responseBody = response.body()
                    if(responseBody != null) {
                        val data = ArrayList<ResultUser>()
                        responseBody.data?.forEach {
                            data.add(ResultUser(it?.id, it?.namaLengkap))
                        }
                        val intent = Intent(this@MainActivity, ScaleActivity::class.java)
                        intent.putExtra("user", data)
                        startActivity(intent)
                    }
                } else {
                    isLoading(false)
                    alertInfo(true)
                    if(response.code() == 404) {
                        binding.loginInfoAlert.setBackgroundResource(R.drawable.alert_warning)
                        binding.alertInfo.text = getString(R.string.nasabah_not_found)
                    } else {
                        binding.loginInfoAlert.setBackgroundResource(R.drawable.alert_danger)
                        binding.alertInfo.text = getString(R.string.server_fault)
                    }
                    binding.alertInfo.setTextColor(Color.WHITE)
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        alertInfo(false)
                    }, 4000)
                }
            }

            override fun onFailure(call: Call<ResponseSearchUsers>, t: Throwable) {
                isLoading(false)
                TODO("Not yet implemented")
            }

        })
    }
}