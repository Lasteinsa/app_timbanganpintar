package com.banksampahteratai.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        utility = Utility()
        utility.checkAuth(preference, this@MainActivity)

        supportActionBar?.hide()

        setupAction()
    }

    private fun setupAction() {
        binding.searchUser.setOnClickListener {
            binding.searchUser.onActionViewExpanded()
        }
        binding.searchUser.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
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
                }
            }

            override fun onFailure(call: Call<ResponseSearchUsers>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
}