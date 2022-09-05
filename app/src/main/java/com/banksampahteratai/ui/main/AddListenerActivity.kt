package com.banksampahteratai.ui.main

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.banksampahteratai.data.api.ResultUser
import com.banksampahteratai.data.model.SampahModel
import com.banksampahteratai.databinding.ActivityAddListenerBinding

class AddListenerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddListenerBinding
    private var sampah: ArrayList<SampahModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddListenerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        binding.submitIt.setOnClickListener {
            val intent = Intent(this@AddListenerActivity, ScaleActivity::class.java)
            val newDataSampah = SampahModel("Jenis Sampah ${(10..100).random()}", 2, 3000, 6000)
            sampah.add(newDataSampah)
            intent.putExtra("sampah", sampah)
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
        binding.cancelIt.setOnClickListener {
            finish()
        }
    }
}