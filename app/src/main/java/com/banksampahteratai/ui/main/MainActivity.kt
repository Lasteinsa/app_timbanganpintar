package com.banksampahteratai.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.banksampahteratai.R
import com.banksampahteratai.data.DataPreference
import com.banksampahteratai.data.DataPreference.Companion.FIRST_TIME
import com.banksampahteratai.data.Utility
import com.banksampahteratai.data.api.ApiConfig
import com.banksampahteratai.data.api.ResponseDataSampah
import com.banksampahteratai.data.api.ResponseKategoriSampah
import com.banksampahteratai.data.api.ResponseSearchUsers
import com.banksampahteratai.data.database.DataSampah
import com.banksampahteratai.data.database.KategoriSampah
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
        mainViewModel.getAllNasabah().observe(this) { nasabahList ->
            if (nasabahList != null) {
                nasabahAdapter.setListNasabah(nasabahList)
                isNasabahEmpty(false)
            }
            if(nasabahAdapter.itemCount == 0) {
                isNasabahEmpty(true)
            } else {
                isNasabahEmpty(false)
            }
        }

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

        if(preference.firstTime && preference.isLogin) {
            utility.showLoading(this@MainActivity, false)
            mainViewModel.deleteAllNasabah()
            getAllNasabah()
            preference.setPreferenceBoolean(FIRST_TIME, false)
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
            mainViewModel.deleteAllSampah()
            mainViewModel.deleteAllKategoriSampah()
            getAllNasabah()
            getAllDataSampah()
            getAllKategoriSampah()
        }
    }

    private fun isNasabahEmpty(isEmpty: Boolean) {
        binding.constraintLayout5.isVisible = !isEmpty
        binding.noNasabah.isVisible = isEmpty
    }

    private fun searchFromDb(query: String) {
        val searchQuery = "%$query%"
        mainViewModel.searchNasabah(searchQuery).observe(this) {
            it.let { nasabahList ->
                nasabahAdapter.setListNasabah(nasabahList)
            }
        }
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
                        mainViewModel.insertNasabah(nasabah)
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

    private fun getAllDataSampah() {
        val retrofitInstance = ApiConfig.getApiService().getListHargaSampah(preference.getToken.toString())
        retrofitInstance.enqueue(object : Callback<ResponseDataSampah> {
            override fun onResponse(
                call: Call<ResponseDataSampah>,
                response: Response<ResponseDataSampah>
            ) {
                if(response.isSuccessful) {
                    val res = response.body()
                    res?.data?.forEach {
                        mainViewModel.insertDataSampah(
                            DataSampah(
                                idSampah       = it?.id.toString(),
                                idKategori     = it?.idKategori.toString(),
                                kategori       = it?.kategori.toString(),
                                jenisSampah    = it?.jenis.toString(),
                                jumlahSampah   = it?.jumlah.toString().toDouble(),
                                hargaSampah    = it?.harga.toString().toInt(),
                                hargaPusat     = it?.hargaPusat.toString().toInt(),
                                totalHarga     = it?.jumlah.toString().toDouble()
                            )
                        )
                    }
                } else {
                    utility.showSnackbar(this@MainActivity,binding.root, "Error Response", true)
                }
            }

            override fun onFailure(call: Call<ResponseDataSampah>, t: Throwable) {
                utility.showSnackbar(this@MainActivity,binding.root, "Error Response", true)
            }

        })
    }

    private fun getAllKategoriSampah() {
        val retrofitInstance = ApiConfig.getApiService().getKategoriSampah()
        retrofitInstance.enqueue(object : Callback<ResponseKategoriSampah> {
            override fun onResponse(
                call: Call<ResponseKategoriSampah>,
                response: Response<ResponseKategoriSampah>
            ) {
                if(response.isSuccessful) {
                    response.body()?.data?.forEach {
                        mainViewModel.insertKategoriSampah(
                            KategoriSampah(
                                idKategori  = it?.id,
                                name        = it?.name,
                                created_at  = it?.created_at
                            )
                        )
                    }
                } else {
                    utility.showSnackbar(this@MainActivity,binding.root, "Error Response", true)
                }
            }

            override fun onFailure(call: Call<ResponseKategoriSampah>, t: Throwable) {
                utility.showSnackbar(this@MainActivity,binding.root, "Error Response", true)
            }

        })
    }

    private fun obtainViewModel(activity: AppCompatActivity): MainViewModel {
        val factory = ViewModelFactory.getInstance(activity.application, preference)
        return ViewModelProvider(activity, factory)[MainViewModel::class.java]
    }
}