package com.banksampahteratai.ui.main

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.banksampahteratai.R
import com.banksampahteratai.data.api.ResultUser
import com.banksampahteratai.data.model.SampahModel
import com.banksampahteratai.databinding.ActivityScaleBinding
import com.banksampahteratai.ui.adapter.AdapterListSampah

class ScaleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScaleBinding
    private lateinit var adapterListSampah: AdapterListSampah
    private lateinit var adapterList: RecyclerView
    private val sampah: ArrayList<SampahModel> = ArrayList()
    private val user: ArrayList<ResultUser> = ArrayList()
    private var harga: Int = 0
    private var total: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Nasabah"
        setupUser()
        setupList()
        setupAction()
    }

    private fun setupUser() {
        val userData = intent.extras?.getParcelableArrayList<ResultUser>("user")
        userData?.forEach {
            user.add(ResultUser(it.id, it.namaLengkap))
            binding.idNasabah.text      = it.id
            binding.namaNasabah.text    = it.namaLengkap
        }
    }

    private fun setupList() {
        adapterList = binding.listSampah
        adapterListSampah   = AdapterListSampah(arrayListOf())
        adapterList.adapter = adapterListSampah
    }

    private fun setupRecycleSampah(sampahData:  ArrayList<SampahModel>?) {
        sampahData?.forEach {
            sampah.add(SampahModel(it.jenisSampah, it.jumlahSampah, it.hargaSampah, it.hasilSampah))
        }

        total = 0
        harga = 0
        sampah.forEach {
            total += it.jumlahSampah
            harga += it.hasilSampah
        }

        adapterListSampah.setData(sampah)

        binding.sumHarga.text   = "Rp. ${harga}"
        binding.sumSampah.text  = "${total} Kg."
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
    }

    private fun resetSampah() {
        sampah.clear()
        total = 0
        harga = 0
        binding.sumHarga.text   = "Rp. ${harga}"
        binding.sumSampah.text  = "${total} Kg."
        adapterListSampah.clearData()
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val sampahData = result.data?.extras?.getParcelableArrayList<SampahModel>("sampah")
            setupRecycleSampah(sampahData)
        }
    }

    private fun openAddListenerActivity() {
        val intent = Intent(this, AddListenerActivity::class.java)
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