package com.banksampahteratai.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.banksampahteratai.R
import com.banksampahteratai.data.Const.Companion.USER
import com.banksampahteratai.data.DataPreference
import com.banksampahteratai.data.Utility
import com.banksampahteratai.data.api.ApiConfig
import com.banksampahteratai.data.api.ResponseSearchUsers
import com.banksampahteratai.data.model.User
import com.banksampahteratai.databinding.ActivityMainBinding
import com.banksampahteratai.ui.login.LoginActivity
import com.bumptech.glide.Glide
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
        utility.showLoading(this, false)
        if(preference.isLogin) {
            utility.checkAuth(preference, this@MainActivity, binding.root)
            utility.hideLoading()
        } else {
            utility.hideLoading()
            val intent = Intent(this@MainActivity, LoginActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }

        Glide.with(this).load(getDrawable(R.drawable.main)).centerCrop().into(binding.mainBackground)

        setupAction()
    }

    private fun setupAction() {
        binding.searchUser.setOnClickListener {
            binding.searchUser.onActionViewExpanded()
        }
        binding.searchUser.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                utility.showLoading(this@MainActivity,false)
                searchUsers(p0)
                binding.searchUser.clearFocus()
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
                    utility.hideLoading()
                    val responseBody = response.body()
                    if(responseBody != null) {
                        val data = ArrayList<User>()
                        responseBody.data?.forEach {
                            data.add(User(it?.id, it?.namaLengkap))
                        }
                        val intent = Intent(this@MainActivity, ScaleActivity::class.java)
                        intent.putExtra(USER, data)
                        startActivity(intent)
                    }
                } else {
                    utility.hideLoading()
                    if(response.code() == 404) {
                        utility.showSnackbar(this@MainActivity, binding.root, getString(R.string.nasabah_not_found), false)
                    } else {
                        utility.showSnackbar(this@MainActivity, binding.root, getString(R.string.server_fault), true)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseSearchUsers>, t: Throwable) {
                utility.hideLoading()
                utility.showSnackbar(this@MainActivity, binding.root, getString(R.string.no_internet), true)
            }

        })
    }
}