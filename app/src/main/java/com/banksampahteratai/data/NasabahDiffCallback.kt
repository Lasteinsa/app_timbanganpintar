package com.banksampahteratai.data

import androidx.recyclerview.widget.DiffUtil
import com.banksampahteratai.data.database.Nasabah

class NasabahDiffCallback(private val mOldNasabah: List<Nasabah>, private val mNewNasabah: List<Nasabah>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return mOldNasabah.size
    }
    override fun getNewListSize(): Int {
        return mNewNasabah.size
    }
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldNasabah[oldItemPosition].id == mNewNasabah[newItemPosition].id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldEmployee = mOldNasabah[oldItemPosition]
        val newEmployee = mNewNasabah[newItemPosition]
        return oldEmployee.idNasabah == newEmployee.idNasabah && oldEmployee.name == newEmployee.name
    }
}