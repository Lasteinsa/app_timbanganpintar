package com.banksampahteratai.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.banksampahteratai.data.model.KategoriSampah
import com.banksampahteratai.data.model.SampahModel
import com.banksampahteratai.databinding.ActivityAddListenerBinding

class AddListenerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddListenerBinding
    private var kategoriSampah: ArrayList<KategoriSampah> = ArrayList()
    private var sampah: ArrayList<SampahModel> = ArrayList()
    private var idSampah: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddListenerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupKategori()
        setupAction()
    }

    private fun setupKategori() {
        val getKategoriSampah = intent.extras?.getParcelableArrayList<KategoriSampah>("kategoriSampah")
        val listKategori = mutableListOf<String?>()
        getKategoriSampah?.forEach {
            kategoriSampah.add(KategoriSampah(it.id, it.name, it.createdAt))
            listKategori.add(it.name)
        }
        val arrayAdapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listKategori )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.jenisSampah.adapter = arrayAdapter
    }

    private fun setupAction() {
        binding.submitIt.setOnClickListener {
            val intent = Intent(this@AddListenerActivity, ScaleActivity::class.java)
            val newDataSampah = SampahModel("Jenis Sampah ${(10..100).random()}", 2, 3000, 6000)
            sampah.add(newDataSampah)
            idSampah = binding.jenisSampah.selectedItem.toString()
            intent.putExtra("sampah", sampah)
            intent.putExtra("kategoriSampah", idSampah)
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
        binding.cancelIt.setOnClickListener {
            finish()
        }
    }
}