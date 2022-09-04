package com.banksampahteratai.ui.main

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.banksampahteratai.R
import com.banksampahteratai.data.api.ResultUser
import com.banksampahteratai.data.model.SampahModel
import com.banksampahteratai.databinding.ActivityScaleBinding
import com.banksampahteratai.ui.adapter.AdapterListSampah
import com.banksampahteratai.ui.login.LoginActivity
import kotlinx.coroutines.NonCancellable.cancel

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
        setupRecycleSampah()
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

    private fun setupRecycleSampah() {
        val sampahData = intent.extras?.getParcelableArrayList<SampahModel>("sampah")
        sampahData?.forEach {
            sampah.add(SampahModel(it.jenisSampah, it.jumlahSampah, it.hargaSampah, it.hasilSampah))
        }

        sampah.forEach {
            total += it.jumlahSampah
            harga += it.hasilSampah
        }

        adapterListSampah.setData(sampah)

        binding.sumHarga.text   = "Rp. ${harga}"
        binding.sumSampah.text  = "${total} Kg."
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_listener -> {
                val intent = Intent(this, AddListenerActivity::class.java)
                intent.putExtra("nextsampah", sampah)
                intent.putExtra("user", user)
                startActivity(intent)
                finish()
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