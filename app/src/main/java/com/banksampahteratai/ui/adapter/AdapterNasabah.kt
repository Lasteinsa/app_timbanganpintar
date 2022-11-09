package com.banksampahteratai.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.banksampahteratai.data.NasabahDiffCallback
import com.banksampahteratai.data.database.Nasabah
import com.banksampahteratai.databinding.ItemNasabahBinding

class AdapterNasabah : RecyclerView.Adapter<AdapterNasabah.NasabahViewHolder>() {
    private val listNasabah = ArrayList<Nasabah>()
    fun setListNasabah(listNasabah: List<Nasabah>) {
        val diffCallback = NasabahDiffCallback(this.listNasabah, listNasabah)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listNasabah.clear()
        this.listNasabah.addAll(listNasabah)
        diffResult.dispatchUpdatesTo(this)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NasabahViewHolder {
        val binding = ItemNasabahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NasabahViewHolder(binding)
    }
    override fun onBindViewHolder(holder: NasabahViewHolder, position: Int) {
        holder.bind(listNasabah[position])
    }
    override fun getItemCount(): Int {
        return listNasabah.size
    }
    inner class NasabahViewHolder(private val binding: ItemNasabahBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(nasabah: Nasabah) {
            with(binding) {
                nasabahName.text = nasabah.name
            }
        }
    }
}