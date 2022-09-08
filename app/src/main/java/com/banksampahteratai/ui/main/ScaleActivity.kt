package com.banksampahteratai.ui.main

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.banksampahteratai.R
import com.banksampahteratai.data.DataPreference
import com.banksampahteratai.data.api.ApiConfig
import com.banksampahteratai.data.api.ResponseDataSampah
import com.banksampahteratai.data.api.ResponseKategoriSampah
import com.banksampahteratai.data.model.*
import com.banksampahteratai.databinding.ActivityScaleBinding
import com.banksampahteratai.ui.adapter.AdapterListSampah
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScaleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScaleBinding
    private lateinit var adapterListSampah: AdapterListSampah
    private lateinit var adapterList: RecyclerView
    private lateinit var preference: DataPreference
    private val listHargaSampah: ArrayList<SampahModel> = ArrayList()
    private val listKategoriSampah: ArrayList<KategoriSampahModel> = ArrayList()
    private val sampah: ArrayList<SampahShow> = ArrayList()
    private val dataTransaksi: ArrayList<TransaksiData> = ArrayList()
    private val user: ArrayList<User> = ArrayList()
    private var nameNasabah: String = ""
    private var idNasabah: String? = ""
    private var date: String = ""
    private var idSampah: Int = 0
    private var harga: Double = 0.0
    private var total: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preference = DataPreference(this)

        supportActionBar?.title = "Nasabah"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this,R.color.teratai_main)))
        setupUser()
        setupList()
        setupAction()
        setupListHargaSampah()
    }

    private fun isLoading(load: Boolean) {
        if(load) {
            binding.loadingLogin.root.visibility = View.VISIBLE
            binding.loadingLogin.root.bringToFront()
        } else {
            binding.loadingLogin.root.visibility = View.INVISIBLE
        }
    }

    private fun setupListHargaSampah() {
        isLoading(true)
        val retrofitInstanceGetListHargaSampah = ApiConfig.getApiService().getListHargaSampah(preference.getToken.toString())
        retrofitInstanceGetListHargaSampah.enqueue(object: Callback<ResponseDataSampah> {
            override fun onResponse(
                call: Call<ResponseDataSampah>,
                response: Response<ResponseDataSampah>
            ) {
                isLoading(false)
                if(response.isSuccessful) {
                    val responseBody = response.body()?.data
                    responseBody?.forEach {
                        listHargaSampah.add(SampahModel(it?.id, it?.idKategori, it?.kategori, it?.jenis, it?.harga?.toInt(), it?.hargaPusat?.toInt(), it?.jumlah?.toDouble()))
                    }
                } else {
                    Toast.makeText(this@ScaleActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDataSampah>, t: Throwable) {
                isLoading(false)
                Toast.makeText(this@ScaleActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        isLoading(true)
        val retrofitInstanceGetKategoriSampah = ApiConfig.getApiService().getKategoriSampah()
        retrofitInstanceGetKategoriSampah.enqueue(object : Callback<ResponseKategoriSampah> {
            override fun onResponse(
                call: Call<ResponseKategoriSampah>,
                response: Response<ResponseKategoriSampah>
            ) {
                isLoading(false)
                if(response.isSuccessful) {
                    val responseBody = response.body()?.data
                    responseBody?.forEach {
                        listKategoriSampah.add(KategoriSampahModel(it?.id, it?.name, it?.created_at))
                    }
                } else {
                    Toast.makeText(this@ScaleActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseKategoriSampah>, t: Throwable) {
                isLoading(false)
                Toast.makeText(this@ScaleActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setupUser() {
        val userData = intent.extras?.getParcelableArrayList<User>("user")
        userData?.forEach { dataUser ->
            user.add(User(dataUser.id, dataUser.namaLengkap))
            idNasabah   = dataUser.id
            nameNasabah = dataUser.namaLengkap.toString()
                .split(' ').joinToString(" ") { char ->
                    char.replaceFirstChar { it.uppercase() }
                }
        }

        binding.idNasabah.text      = idNasabah
        binding.namaNasabah.text    = nameNasabah
    }

    private fun setupList() {
        adapterList = binding.listSampah
        adapterListSampah   = AdapterListSampah(arrayListOf(), arrayListOf())
        adapterList.adapter = adapterListSampah

        adapterListSampah.setOnCallbackInterface(object: AdapterListSampah.CallbackInterface {
            override fun passSampah(dataSampah: ArrayList<SampahShow>, listTransaksiSampah: ArrayList<TransaksiData>) {
                sampah.clear()
                sampah.addAll(dataSampah)
                dataTransaksi.clear()
                dataTransaksi.addAll(listTransaksiSampah)
                reCalculatePlease()
            }
        })
    }

    private fun setupRecycleSampah(sampahData:  ArrayList<SampahShow>?, transaksiData: ArrayList<TransaksiData>?) {
        sampahData?.forEach {
            sampah.add(SampahShow(it.jenisSampah, it.jumlahSampah, it.hargaSampah, it.totalHarga))
        }
        transaksiData?.forEach {
            dataTransaksi.add(TransaksiData(it.idSampah, it.jumlah))
        }
        adapterListSampah.setData(sampah, dataTransaksi)

        reCalculatePlease()
    }

    private fun setupAction() {
        binding.btnCancel.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.sure_to_delete))
                setMessage(getString(R.string.data_will_be_lost))
                setCancelable(false)
                setPositiveButton(getString(R.string.confirm_yes), DialogInterface.OnClickListener { _, _ ->
                    resetSampah()
                })
                setNegativeButton(getString(R.string.confirm_no), DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                })
                create()
                show()
            }
        }
        binding.btnSubmit.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Submit?")
                setMessage("test")
                setCancelable(false)
                setPositiveButton(getString(R.string.confirm_yes), DialogInterface.OnClickListener { _, _ ->
                    submitSampah()
                })
                setNegativeButton(getString(R.string.confirm_no), DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                })
                create()
                show()
            }
        }
    }

    private fun submitSampah() {

    }

    private fun resetSampah() {
        sampah.clear()
        total = 0.0
        harga = 0.0
        binding.sumHarga.text   = "Rp. ${harga}"
        binding.sumSampah.text  = "${total} Kg."
        adapterListSampah.clearData()
    }

    private fun reCalculatePlease() {
        total = 0.0
        harga = 0.0
        sampah.forEach {
            total += it.jumlahSampah
            harga += (it.jumlahSampah * it.hargaSampah)
        }
        binding.sumHarga.text   = "Rp. ${harga}"
        binding.sumSampah.text  = "${total} Kg."
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val sampahData = if (Build.VERSION.SDK_INT >= 33) {
                result.data?.extras?.getParcelableArrayList("sampah", TransaksiData::class.java)
            } else {
                result.data?.extras?.getParcelableArrayList("sampah")
            }
            val sampahShow = if (Build.VERSION.SDK_INT >= 33) {
                result.data?.extras?.getParcelableArrayList<SampahShow>("sampahShow", SampahShow::class.java)
            } else {
                result.data?.extras?.getParcelableArrayList("sampahShow")
            }
            setupRecycleSampah(sampahShow, sampahData)
        }
    }

    private fun openAddListenerActivity() {
        val intent = Intent(this, AddListenerActivity::class.java)
        intent.putParcelableArrayListExtra("listHargaSampah", listHargaSampah)
        intent.putParcelableArrayListExtra("kategoriSampah", listKategoriSampah)
        resultLauncher.launch(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_listener -> {
                openAddListenerActivity()
                return true
            }
            else ->return true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.title_sure_back))
            setMessage(getString(R.string.data_will_be_lost))
            setCancelable(false)
            setPositiveButton(getString(R.string.confirm_yes), DialogInterface.OnClickListener { _, _ ->
                super.onBackPressed()
                finish()
            })
            setNegativeButton(getString(R.string.confirm_no), DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            })
            create()
            show()
        }
    }
}