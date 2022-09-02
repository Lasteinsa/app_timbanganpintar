package com.banksampahteratai.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.banksampahteratai.R
import com.banksampahteratai.data.api.ResultUser
import com.banksampahteratai.databinding.ActivityScaleBinding

class ScaleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScaleBinding
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

        val listSampah = ArrayList<String>()
        for(i in 1..20) {
            listSampah.add("Sampah ${i}")
        }

        binding.lvSampah.adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, listSampah)
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
}