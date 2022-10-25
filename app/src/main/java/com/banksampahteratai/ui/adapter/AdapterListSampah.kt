package com.banksampahteratai.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.banksampahteratai.data.model.SampahShow
import com.banksampahteratai.data.model.TransaksiData
import com.banksampahteratai.databinding.AdapterListSampahBinding
import com.banksampahteratai.ui.adapter.AdapterListSampah.ViewHolder

class AdapterListSampah(val sampah: ArrayList<SampahShow>, val listTransaksiSampah: ArrayList<TransaksiData>): RecyclerView.Adapter<ViewHolder>() {
    private lateinit var callbackInterface: CallbackInterface

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterListSampahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = sampah[position]
        val listTransaksi = listTransaksiSampah[position]
        holder.binding.jenisSampah.text     = data.jenisSampah
        holder.binding.kalkulasiSampah.text = "${data.jumlahSampah} X ${data.hargaSampah} = ${data.totalHarga}"
        holder.binding.btnDeleteItem.setOnClickListener {
            sampah.remove(data)
            listTransaksiSampah.remove(listTransaksi)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, sampah.size)
            callbackInterface.passSampah(sampah,listTransaksiSampah)
        }
    }

    override fun getItemCount() = sampah.size

    class ViewHolder(var binding: AdapterListSampahBinding): RecyclerView.ViewHolder(binding.root)

    public fun setData(data: List<SampahShow>, listTransaksi: List<TransaksiData>) {
        sampah.clear()
        listTransaksiSampah.clear()

        data.forEach {
            sampah.add(SampahShow(it.jenisSampah, it.jumlahSampah, it.hargaSampah, it.totalHarga))
        }

        listTransaksi.forEach {
            listTransaksiSampah.add(TransaksiData(it.id_sampah,it.jumlah))
        }

        notifyDataSetChanged()
    }

    public fun clearData() {
        sampah.clear()
        listTransaksiSampah.clear()
        notifyDataSetChanged()
    }

    fun setOnCallbackInterface(callbackInterface: CallbackInterface) {
        this.callbackInterface = callbackInterface
    }

    interface CallbackInterface {
        fun passSampah(dataSampah: ArrayList<SampahShow>, listTransaksiSampah: ArrayList<TransaksiData>)
    }
}