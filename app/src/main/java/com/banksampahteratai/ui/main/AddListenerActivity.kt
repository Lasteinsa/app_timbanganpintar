package com.banksampahteratai.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.banksampahteratai.data.api.ResultUser
import com.banksampahteratai.data.model.SampahModel
import com.banksampahteratai.databinding.ActivityAddListenerBinding

class AddListenerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddListenerBinding
    private var sampah: ArrayList<SampahModel> = ArrayList()
    private var user: ArrayList<ResultUser> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddListenerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userData    = intent.getParcelableArrayListExtra<ResultUser>("user")
        val prevSampah  = intent.getParcelableArrayListExtra<SampahModel>("nextsampah")

        userData?.forEach {
            user.add(ResultUser(it.id, it.namaLengkap))
        }
        prevSampah?.forEach {
            sampah.add(SampahModel(it.jenisSampah, it.jumlahSampah, it.hargaSampah, it.hasilSampah))
        }

        setupAction()
    }

    private fun setupAction() {
        binding.submitIt.setOnClickListener {
            val intent = Intent(this@AddListenerActivity, ScaleActivity::class.java)
            val newDataSampah = SampahModel("Jenis Sampah ${(10..100).random()}", 2, 3000, 6000)
            sampah.add(newDataSampah)
            intent.putExtra("sampah", sampah)
            intent.putExtra("user", user)
            startActivity(intent)
            finish()
        }
    }
}