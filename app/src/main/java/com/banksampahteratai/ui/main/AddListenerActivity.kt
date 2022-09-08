package com.banksampahteratai.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.banksampahteratai.data.model.KategoriSampahModel
import com.banksampahteratai.data.model.SampahModel
import com.banksampahteratai.data.model.SampahShow
import com.banksampahteratai.data.model.TransaksiData
import com.banksampahteratai.databinding.ActivityAddListenerBinding

class AddListenerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddListenerBinding
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private var listHargaSampah: ArrayList<SampahModel> = ArrayList()
    private var kategoriSampah: ArrayList<KategoriSampahModel> = ArrayList()
    private var sampah: ArrayList<TransaksiData> = ArrayList()
    private var sampahShow: ArrayList<SampahShow> = ArrayList()
    private var idSampah: Int = 0
    private var namaJenis: String = ""
    val listKategori = mutableListOf<String?>()
    val listJenis    = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddListenerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupKategori()
        setupAction()
    }

    private fun setupKategori() {
        val getListHargaSampah = intent.extras?.getParcelableArrayList<SampahModel>("listHargaSampah")
        val getKategoriSampah = intent.extras?.getParcelableArrayList<KategoriSampahModel>("kategoriSampah")

        getListHargaSampah?.forEach {
            listHargaSampah.add(SampahModel(it?.id, it?.idKategori, it?.kategori, it?.jenis, it?.harga?.toInt(), it?.hargaPusat?.toInt(), it?.jumlah?.toDouble()))
        }

        getKategoriSampah?.forEach {
            kategoriSampah.add(KategoriSampahModel(it?.id, it?.name, it?.created_at))
            listKategori.add(it.name)
        }

        val listKategoriAdapter = ArrayAdapter(this,androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listKategori )
        listKategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.kategoriSampah.adapter = listKategoriAdapter

        arrayAdapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listJenis )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.jenisSampah.adapter = arrayAdapter

        binding.kategoriSampah.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedKategori = binding.kategoriSampah.selectedItem
                listJenis.clear()
                getListHargaSampah?.forEach {
                    if(it.kategori == selectedKategori) {
                        listJenis.add(it.jenis.toString())
                    }
                }
                arrayAdapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun setupAction() {
        binding.submitIt.setOnClickListener {
            val intent = Intent(this@AddListenerActivity, ScaleActivity::class.java)
            val jumlah = 5

            namaJenis = binding.jenisSampah.selectedItem.toString()

            listHargaSampah.forEach {
                if(it.jenis == namaJenis) {
                    val jenisSampahShow     = it.jenis
                    val jumlahSampahShow    = jumlah.toDouble()
                    val hargaSampahShow     = it.harga!!
                    val totalHargaShow      = jumlahSampahShow * hargaSampahShow
                    val newDataSampah = TransaksiData(it.id, jumlah.toString())
                    val newSampahShow = SampahShow(jenisSampahShow, jumlahSampahShow, hargaSampahShow, totalHargaShow)

                    sampah.add(newDataSampah)
                    sampahShow.add(newSampahShow)
                }
            }
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