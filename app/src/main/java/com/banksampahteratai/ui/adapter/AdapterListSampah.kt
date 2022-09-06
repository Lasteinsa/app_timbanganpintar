package com.banksampahteratai.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.banksampahteratai.data.model.SampahModel
import com.banksampahteratai.databinding.AdapterListSampahBinding
import com.banksampahteratai.ui.adapter.AdapterListSampah.ViewHolder

class AdapterListSampah(val sampah: ArrayList<SampahModel>): RecyclerView.Adapter<ViewHolder>() {
    private lateinit var callbackInterface: CallbackInterface

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterListSampahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = sampah[position]
        holder.binding.jenisSampah.text     = data.jenisSampah
        holder.binding.kalkulasiSampah.text = "${data.jumlahSampah} X ${data.hargaSampah} = ${data.hasilSampah}"
        holder.binding.btnDeleteItem.setOnClickListener {
            sampah.remove(data)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, sampah.size)
            callbackInterface.passSampah(sampah)
        }
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

    fun setOnCallbackInterface(callbackInterface: CallbackInterface) {
        this.callbackInterface = callbackInterface
    }

    interface CallbackInterface {
        fun passSampah(dataSampah: ArrayList<SampahModel>)
    }
}