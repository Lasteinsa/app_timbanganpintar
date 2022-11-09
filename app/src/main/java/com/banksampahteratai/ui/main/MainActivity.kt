package com.banksampahteratai.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.banksampahteratai.R
import com.banksampahteratai.data.DataPreference
import com.banksampahteratai.data.Utility
import com.banksampahteratai.data.api.ApiConfig
import com.banksampahteratai.data.api.ResponseSearchUsers
import com.banksampahteratai.data.database.Nasabah
import com.banksampahteratai.databinding.ActivityMainBinding
import com.banksampahteratai.ui.ViewModelFactory
import com.banksampahteratai.ui.adapter.AdapterNasabah
import com.banksampahteratai.ui.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preference: DataPreference
    private lateinit var nasabahAdapter: AdapterNasabah
    private lateinit var mainViewModel: MainViewModel
    private lateinit var utility: Utility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preference      = DataPreference(this)

        mainViewModel = obtainViewModel(this@MainActivity)
        mainViewModel.getAllNasabah().observe(this, { nasabahList ->
            if (nasabahList != null) {
                nasabahAdapter.setListNasabah(nasabahList)
            }
        })

        nasabahAdapter = AdapterNasabah()
        binding.rvNasabah.layoutManager = LinearLayoutManager(this)
        binding.rvNasabah.adapter = nasabahAdapter

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

        setupAction()
    }

    private fun setupAction() {
        binding.searchUser.setOnClickListener {
            binding.searchUser.onActionViewExpanded()
        }
        binding.searchUser.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    searchFromDb(p0)
                    binding.searchUser.clearFocus()
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if(p0 != null) {
                    searchFromDb(p0)
                }
                return false
            }

        })
        binding.fabRefresh.setOnClickListener {
            utility.showLoading(this@MainActivity, false)
            mainViewModel.deleteAllNasabah()
            getAllNasabah()
        }
    }

    private fun searchFromDb(query: String) {
        val searchQuery = "%$query%"
        mainViewModel.search(searchQuery).observe(this, {
            it.let { nasabahList ->
                nasabahAdapter.setListNasabah(nasabahList)
            }
        })
    }

    private fun getAllNasabah() {
        val retrofitInstance = ApiConfig.getApiService().getAllNasabah(preference.getToken.toString())
        retrofitInstance.enqueue(object: Callback<ResponseSearchUsers> {
            override fun onResponse(
                call: Call<ResponseSearchUsers>,
                response: Response<ResponseSearchUsers>
            ) {
                if(response.isSuccessful) {
                    val res = response.body()
                    res?.data?.forEach {
                        val nasabah = Nasabah()
                        nasabah.let { nasabah ->  
                            nasabah.idNasabah   = it?.id
                            nasabah.name        = it?.namaLengkap
                        }
                        mainViewModel.insert(nasabah)
                    }
                }
                utility.hideLoading()
            }

            override fun onFailure(call: Call<ResponseSearchUsers>, t: Throwable) {
                utility.hideLoading()
                utility.showSnackbar(this@MainActivity, binding.root, getString(R.string.no_internet), true)
            }
        })
    }

    private fun obtainViewModel(activity: AppCompatActivity): MainViewModel {
        val factory = ViewModelFactory.getInstance(activity.application, preference)
        return ViewModelProvider(activity, factory)[MainViewModel::class.java]
    }
}