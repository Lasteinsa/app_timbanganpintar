package com.banksampahteratai.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.banksampahteratai.data.Const
import com.banksampahteratai.data.NasabahDiffCallback
import com.banksampahteratai.data.database.Nasabah
import com.banksampahteratai.data.model.User
import com.banksampahteratai.databinding.ItemNasabahBinding
import com.banksampahteratai.ui.main.ScaleActivity

class AdapterNasabah : RecyclerView.Adapter<AdapterNasabah.NasabahViewHolder>() {
    private val listNasabah = ArrayList<Nasabah>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

    fun setListNasabah(listNasabah: List<Nasabah>) {
        val diffCallback = NasabahDiffCallback(this.listNasabah, listNasabah)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listNasabah.clear()
        this.listNasabah.addAll(listNasabah)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack    = onItemClickCallBack
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
            binding.root.setOnClickListener {
                onItemClickCallBack!!.onItemClicked(nasabah)
            }
            with(binding) {
                nasabahName.text = nasabah.name

                itemView.setOnClickListener {
                    val data = User(nasabah.idNasabah.toString(), nasabah.name)
                    val intent = Intent(itemView.context, ScaleActivity::class.java)
                    intent.putExtra(Const.USER, data)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(nasabah: Nasabah)
    }
}