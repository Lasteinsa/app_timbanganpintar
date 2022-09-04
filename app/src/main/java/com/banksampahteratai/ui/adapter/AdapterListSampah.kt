package com.banksampahteratai.ui.adapter

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.banksampahteratai.R
import com.banksampahteratai.data.model.SampahModel
import com.banksampahteratai.databinding.AdapterListSampahBinding
import com.banksampahteratai.ui.adapter.AdapterListSampah.ViewHolder

class AdapterListSampah(val sampah: ArrayList<SampahModel>): RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterListSampahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = sampah[position]
        holder.binding.jenisSampah.text     = data.jenisSampah
        holder.binding.kalkulasiSampah.text = "${data.jumlahSampah} X ${data.hargaSampah} = ${data.hasilSampah}"
    }

    override fun getItemCount() = sampah.size

    class ViewHolder(var binding: AdapterListSampahBinding): RecyclerView.ViewHolder(binding.root)

    public fun setData(data: List<SampahModel>) {
        sampah.clear()

        data.forEach {
            sampah.add(SampahModel(it.jenisSampah, it.jumlahSampah, it.hargaSampah, it.hasilSampah))
        }
        notifyDataSetChanged()
    }

    public fun clearData() {
        sampah.clear()
        notifyDataSetChanged()
    }
}