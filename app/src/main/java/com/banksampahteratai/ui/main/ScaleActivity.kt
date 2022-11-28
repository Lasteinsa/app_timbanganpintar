package com.banksampahteratai.ui.main

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.banksampahteratai.R
import com.banksampahteratai.data.Const.Companion.KATEGORI_SAMPAH
import com.banksampahteratai.data.Const.Companion.LIST_HARGA_SAMPAH
import com.banksampahteratai.data.Const.Companion.SAMPAH
import com.banksampahteratai.data.Const.Companion.SAMPAH_SHOW
import com.banksampahteratai.data.Const.Companion.USER
import com.banksampahteratai.data.DataPreference
import com.banksampahteratai.data.Utility
import com.banksampahteratai.data.api.ApiConfig
import com.banksampahteratai.data.api.ResponseTransaksi
import com.banksampahteratai.data.model.*
import com.banksampahteratai.databinding.ActivityScaleBinding
import com.banksampahteratai.ui.ViewModelFactory
import com.banksampahteratai.ui.adapter.AdapterListSampah
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScaleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScaleBinding
    private lateinit var utility: Utility
    private lateinit var executor: ExecutorService
    private lateinit var adapterListSampah: AdapterListSampah
    private lateinit var adapterList: RecyclerView
    private lateinit var preference: DataPreference
    private lateinit var submitData: TransaksiModel
    private lateinit var bounceAnim: Animation
    private lateinit var scaleViewModel: ScaleViewModel
    private val listHargaSampah: ArrayList<SampahModel> = ArrayList()
    private val listKategoriSampah: ArrayList<KategoriSampahModel> = ArrayList()
    private val sampah: ArrayList<SampahShow> = ArrayList()
    private val dataTransaksi: ArrayList<TransaksiData> = ArrayList()
    private var nameNasabah: String = ""
    private var idNasabah: String = ""
    private var harga: Double = 0.0
    private var total: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preference  = DataPreference(this)
        utility     = Utility()

        scaleViewModel = obtainViewModel(this@ScaleActivity)

        supportActionBar?.title = "Nasabah"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this,R.color.teratai_main)))

        executor = Executors.newSingleThreadExecutor()

        setSampahAndKategori()
        resetHarga()
        setupUser()
        setupList()
        setupAction()
        setupAnimation()
    }

    private fun setSampahAndKategori() {
        scaleViewModel.getAllDataSampah().observe(this) { sampahList ->
            sampahList?.forEach { dataSampah ->
                listHargaSampah.add(
                    SampahModel(
                        dataSampah.idSampah,
                        dataSampah.idSampah,
                        dataSampah.idKategori,
                        dataSampah.kategori,
                        dataSampah.jenisSampah,
                        dataSampah.hargaSampah,
                        dataSampah.hargaPusat,
                        dataSampah.jumlahSampah.toString().toDouble()
                    )
                )
            }
        }
        scaleViewModel.getAllKategoriSampah().observe(this) { kategoriList ->
            kategoriList?.forEach { dataKategori ->
                listKategoriSampah.add(
                    KategoriSampahModel(
                        dataKategori.idKategori,
                        dataKategori.name,
                        dataKategori.created_at
                    )
                )
            }
        }
    }

    private fun setupAnimation() {
        bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce)
    }

    private fun setupUser() {
        val userData = intent.getParcelableExtra<User>(USER) as User
        idNasabah   = userData.id.toString()
        nameNasabah = userData.namaLengkap.toString()

        binding.idNasabah.text      = idNasabah
        binding.namaNasabah.text    =
            nameNasabah.split(" ").joinToString(" ") { it.replaceFirstChar { word -> word.uppercase() } }
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
            it.startAnimation(bounceAnim)
            utility.showDialog(
                this@ScaleActivity,
                getString(R.string.sure_to_delete), getString(R.string.data_will_be_lost),
                getString(R.string.confirm_yes), getString(R.string.confirm_no), true,
                ::finish
            )
        }
        binding.btnSubmit.setOnClickListener {
            it.startAnimation(bounceAnim)
            if(dataTransaksi.isEmpty()) {
                utility.showSnackbar(this@ScaleActivity, binding.root, "Tambahkan Setidaknya Input Transaksi", true)
            } else {
                utility.showDialog(
                    this,
                    getString(R.string.want_submit), getString(R.string.data_will_be_send),
                    getString(R.string.confirm_yes), getString(R.string.confirm_no), true,
                    ::submitSampah
                )
            }
        }
    }

    private fun submitSampah() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            utility.showLoading(this,false)
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
                    utility.hideLoading()
                    if(response.isSuccessful) {
                        utility.showDialog(this@ScaleActivity,getString(R.string.success), response.body()?.messages.toString(), getString(R.string.OK), "", false, ::finish)
                    } else {
                        utility.showDialog(this@ScaleActivity, getString(R.string.error), response.message(), getString(R.string.OK), "Cancel", false, ::submitSampah)
                    }
                }

                override fun onFailure(call: Call<ResponseTransaksi>, t: Throwable) {
                    utility.hideLoading()
                    utility.showDialog(this@ScaleActivity, getString(R.string.failed), getString(R.string.failed_send_data), getString(R.string.OK), getString(R.string.bigno), true, ::submitSampah)
                }

            })

        } else {

        }
    }

    private fun resetHarga() {
        binding.sumHarga.text   = "Rp. ${harga}"
        binding.sumSampah.text  = "${total} Kg."
    }

    private fun reCalculatePlease() {
        total = 0.0
        harga = 0.0
        sampah.forEach {
            total += it.jumlahSampah
            harga += (it.jumlahSampah * it.hargaSampah)
        }
        resetHarga()
        if(sampah.isEmpty()) {
            binding.listSampah.isVisible    = false
            binding.noTransaction.isVisible = true
        } else {
            binding.listSampah.isVisible    = true
            binding.noTransaction.isVisible = false
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val sampahData = if (Build.VERSION.SDK_INT >= 33) {
                result.data?.extras?.getParcelableArrayList(SAMPAH, TransaksiData::class.java)
            } else {
                result.data?.extras?.getParcelableArrayList(SAMPAH)
            }
            val sampahShow = if (Build.VERSION.SDK_INT >= 33) {
                result.data?.extras?.getParcelableArrayList<SampahShow>(SAMPAH_SHOW, SampahShow::class.java)
            } else {
                result.data?.extras?.getParcelableArrayList(SAMPAH_SHOW)
            }
            setupRecycleSampah(sampahShow, sampahData)
        } else if(result.resultCode == Activity.RESULT_CANCELED) {
            utility.showSnackbar(this@ScaleActivity, binding.root, "Canceled", false)
        }
    }

    private fun openAddListenerActivity() {
        val intent = Intent(this, AddListenerActivity::class.java)
        intent.putParcelableArrayListExtra(LIST_HARGA_SAMPAH, listHargaSampah)
        intent.putParcelableArrayListExtra(KATEGORI_SAMPAH, listKategoriSampah)
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

    private fun obtainViewModel(activity: AppCompatActivity): ScaleViewModel {
        val factory = ViewModelFactory.getInstance(activity.application, preference)
        return ViewModelProvider(activity, factory)[ScaleViewModel::class.java]
    }
}