package com.banksampahteratai.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle("Nasabah")

        val user = intent.extras?.getParcelableArrayList<ResultUser>("user")
        user?.forEach {
            binding.idNasabah.text = it.id
            binding.namaNasabah.text = it.namaLengkap
        }
        val sampah = ArrayList<SampahModel>()

        for(i in 1..10) {
            sampah.add(SampahModel("Sampah ${i}", i, 100 + i, 200 + i))

        }
        var total = 0
        var harga = 0
        sampah.forEach {
            total += it.jumlahSampah
            harga += it.hargaSampah
        }

        binding.sumHarga.text = "Rp. ${harga}"
        binding.sumSampah.text = "${total} Kg."

        setupList()

        adapterListSampah.setData(sampah)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favorite -> {
                val moveToFavorite = Intent(this, AddListenerActivity::class.java)
                startActivity(moveToFavorite)
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

    private fun setupList() {
        adapterList = binding.listSampah
        adapterListSampah = AdapterListSampah(arrayListOf())
        adapterList.adapter = adapterListSampah
    }
}