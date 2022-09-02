package com.banksampahteratai.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.banksampahteratai.data.api.ResultUser
import com.banksampahteratai.databinding.ActivityScaleBinding

class ScaleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScaleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScaleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val user = intent.extras?.getParcelableArrayList<ResultUser>("user")
        user?.forEach {
            binding.idNasabah.text = it.id
            binding.namaNasabah.text = it.namaLengkap
        }
    }
}