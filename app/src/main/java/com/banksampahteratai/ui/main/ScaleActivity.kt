package com.banksampahteratai.ui.main

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
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
import com.banksampahteratai.data.api.ApiConfig
import com.banksampahteratai.data.api.ResponseKategoriSampah
import com.banksampahteratai.data.model.KategoriSampah
import com.banksampahteratai.data.model.SampahModel
import com.banksampahteratai.data.model.User
import com.banksampahteratai.databinding.ActivityScaleBinding
import com.banksampahteratai.ui.adapter.AdapterListSampah
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScaleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScaleBinding
    private lateinit var adapterListSampah: AdapterListSampah
    private lateinit var adapterList: RecyclerView
    private val kategoriSampah: ArrayList<KategoriSampah> = ArrayList()
    private val sampah: ArrayList<SampahModel> = ArrayList()
    private val user: ArrayList<User> = ArrayList()
    private var nameNasabah: String = ""
    private var idNasabah: String? = ""
    private var date: String = ""
    private var idSampah: String = ""
    private var harga: Int = 0
    private var total: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Nasabah"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this,R.color.teratai_main)))
        setupUser()
        setupList()
        setupAction()
        setupKategoriSampah()
    }

    private fun isLoading(load: Boolean) {
        if(load) {
            binding.loadingLogin.root.visibility = View.VISIBLE
            binding.loadingLogin.root.bringToFront()
        } else {
            binding.loadingLogin.root.visibility = View.INVISIBLE
        }
    }

    private fun setupKategoriSampah() {
        isLoading(true)
        val retrofitInstance = ApiConfig.getApiService().getKategoriSampah()
        retrofitInstance.enqueue(object: Callback<ResponseKategoriSampah> {
            override fun onResponse(
                call: Call<ResponseKategoriSampah>,
                response: Response<ResponseKategoriSampah>
            ) {
                isLoading(false)
                if(response.isSuccessful) {
                    val responseBody = response.body()?.data
                    responseBody?.forEach {
                        kategoriSampah.add(KategoriSampah(it?.id, it?.name, it?.createdAt))
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
        adapterListSampah   = AdapterListSampah(arrayListOf())
        adapterList.adapter = adapterListSampah

        adapterListSampah.setOnCallbackInterface(object: AdapterListSampah.CallbackInterface {
            override fun passSampah(dataSampah: ArrayList<SampahModel>) {
                sampah.clear()
                sampah.addAll(dataSampah)
                reCalculatePlease()
            }
        })
    }

    private fun setupRecycleSampah(sampahData:  ArrayList<SampahModel>?) {
        sampahData?.forEach {
            sampah.add(SampahModel(it.jenisSampah, it.jumlahSampah, it.hargaSampah, it.hasilSampah))
        }
        adapterListSampah.setData(sampah)

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
        total = 0
        harga = 0
        binding.sumHarga.text   = "Rp. ${harga}"
        binding.sumSampah.text  = "${total} Kg."
        adapterListSampah.clearData()
    }

    private fun reCalculatePlease() {
        total = 0
        harga = 0
        sampah.forEach {
            total += it.jumlahSampah
            harga += it.hasilSampah
        }
        binding.sumHarga.text   = "Rp. ${harga}"
        binding.sumSampah.text  = "${total} Kg."
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val sampahData = result.data?.extras?.getParcelableArrayList<SampahModel>("sampah")
            val returnId = result.data?.extras?.getInt("kategoriSampah")
            idSampah = kategoriSampah[returnId!!].id.toString()
            setupRecycleSampah(sampahData)
        }
    }

    private fun openAddListenerActivity() {
        val intent = Intent(this, AddListenerActivity::class.java)
        intent.putParcelableArrayListExtra("kategoriSampah", kategoriSampah)
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