package com.banksampahteratai.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.banksampahteratai.data.model.SampahModel
import com.banksampahteratai.data.model.SampahShow
import com.banksampahteratai.data.model.TransaksiData
import com.banksampahteratai.databinding.ActivityAddListenerBinding

class AddListenerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddListenerBinding
    private var kategoriSampah: ArrayList<SampahModel> = ArrayList()
    private var sampah: ArrayList<TransaksiData> = ArrayList()
    private var sampahShow: ArrayList<SampahShow> = ArrayList()
    private var idSampah: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddListenerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupKategori()
        setupAction()
    }

    private fun setupKategori() {
        val getKategoriSampah = intent.extras?.getParcelableArrayList<SampahModel>("kategoriSampah")
        val listKategori = mutableListOf<String?>()
        getKategoriSampah?.forEach {
            kategoriSampah.add(SampahModel(it?.id, it?.idKategori, it?.kategori, it?.jenis, it?.harga?.toInt(), it?.hargaPusat?.toInt(), it?.jumlah?.toDouble()))
            listKategori.add(it.jenis)
        }
        val arrayAdapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listKategori )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.jenisSampah.adapter = arrayAdapter
    }

    private fun setupAction() {
        binding.submitIt.setOnClickListener {
            val intent = Intent(this@AddListenerActivity, ScaleActivity::class.java)
            val jumlah = 5

            idSampah = binding.jenisSampah.selectedItemId.toInt()

            val jenisSampahShow     = kategoriSampah[idSampah].jenis.toString()
            val jumlahSampahShow    = jumlah.toDouble()
            val hargaSampahShow     = kategoriSampah[idSampah].harga!!.toInt()
            val totalHargaShow      = jumlahSampahShow * hargaSampahShow

            val newDataSampah = TransaksiData(kategoriSampah[idSampah].id, jumlah.toString())
            val newSampahShow = SampahShow(jenisSampahShow, jumlahSampahShow, hargaSampahShow, totalHargaShow)

            sampah.add(newDataSampah)
            sampahShow.add(newSampahShow)
            intent.putExtra("sampah", sampah)
            intent.putExtra("sampahShow", sampahShow)
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
        binding.cancelIt.setOnClickListener {
            finish()
        }
    }
}