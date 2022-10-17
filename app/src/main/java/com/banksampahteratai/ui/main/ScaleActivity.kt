package com.banksampahteratai.ui.main

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
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
import com.banksampahteratai.data.api.*
import com.banksampahteratai.data.model.*
import com.banksampahteratai.databinding.ActivityScaleBinding
import com.banksampahteratai.ui.adapter.AdapterListSampah
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScaleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScaleBinding
    private lateinit var adapterListSampah: AdapterListSampah
    private lateinit var adapterList: RecyclerView
    private lateinit var preference: DataPreference
    private lateinit var submitData: TransaksiModel
    private val listHargaSampah: ArrayList<SampahModel> = ArrayList()
    private val listKategoriSampah: ArrayList<KategoriSampahModel> = ArrayList()
    private val sampah: ArrayList<SampahShow> = ArrayList()
    private val dataTransaksi: ArrayList<TransaksiData> = ArrayList()
    private val user: ArrayList<User> = ArrayList()
    private var nameNasabah: String = ""
    private var idNasabah: String? = ""
    private var harga: Double = 0.0
    private var total: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preference = DataPreference(this)

        supportActionBar?.title = "Nasabah"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this,R.color.teratai_main)))

        resetHarga()
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
                    showDialog("Kesalahan","Gagal mendapatkan List Harga. Coba Lagi?", "OK", "CANCEL", true, ::setupListHargaSampah)
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
                    showDialog("Kesalahan","Gagal mendapatkan Kategori Sampah. Coba Lagi?", "OK", "CANCEL", true, ::setupListHargaSampah)
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
            dataTransaksi.add(TransaksiData(it.id_sampah, it.jumlah))
        }
        adapterListSampah.setData(sampah, dataTransaksi)

        reCalculatePlease()
    }

    private fun setupAction() {
        binding.btnCancel.setOnClickListener {
            showDialog(
                getString(R.string.sure_to_delete), getString(R.string.data_will_be_lost),
                getString(R.string.confirm_yes), getString(R.string.confirm_no), true,
                ::finish
            )
        }
        binding.btnSubmit.setOnClickListener {
            showDialog(
                "Submit?", "Data akan dikirim",
                getString(R.string.confirm_yes), getString(R.string.confirm_no), true,
                ::submitSampah
            )
        }
    }

    private fun submitSampah() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            isLoading(true)
            val current = LocalDateTime.now()
            val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
            val date = current.format(dateFormat)
            submitData = TransaksiModel(idNasabah,date,dataTransaksi)

            val retrofitInstanceSetorSampah = ApiConfig.getApiService().setorSampah(preference.getToken.toString(), submitData)
            retrofitInstanceSetorSampah.enqueue(object: Callback<ResponseTransaksi> {
                override fun onResponse(
                    call: Call<ResponseTransaksi>,
                    response: Response<ResponseTransaksi>
                ) {
                    isLoading(false)
                    if(response.isSuccessful) {
                        showDialog("Sukses", response.body()?.messages.toString(), "OK", "", false, ::finish)
                    } else {
                        showDialog("Error", response.message(), "OK", "", false, ::submitSampah)
                    }
                }

                override fun onFailure(call: Call<ResponseTransaksi>, t: Throwable) {
                    isLoading(false)
                    showDialog("Gagal", "Telah gagal mengirim data. Ulang?", "OK", "Tidak", true, ::submitSampah)
                }

            })

        } else {

        }
    }

    private fun resetHarga() {
        binding.sumHarga.text   = "Rp. ${harga}"
        binding.sumSampah.text  = "${total} Kg."
    }

    private fun showDialog(titleDialog: String, messageDialog: String, confirmMessage: String, cancelMessage: String?, cancelable: Boolean, doFunc: ()-> Unit) {
        AlertDialog.Builder(this).apply {
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

    private fun reCalculatePlease() {
        total = 0.0
        harga = 0.0
        sampah.forEach {
            total += it.jumlahSampah
            harga += (it.jumlahSampah * it.hargaSampah)
        }
        resetHarga()
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