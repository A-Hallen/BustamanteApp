package com.hallen.bustamante.ui.recyclers.diffs

import androidx.recyclerview.widget.DiffUtil
import com.hallen.bustamante.data.model.Proveedor

class TcpDiffCallback(
    private val oldList: List<Proveedor>,
    private val newList: List<Proveedor>
) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].proveedorId == newList[newItemPosition].proveedorId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].nombre == newList[newItemPosition].nombre
    }

}
