package com.example.bustamante.ui.recyclers.adapters

import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.bustamante.data.model.Proveedor
import com.example.bustamante.databinding.ProductHeaderBinding
import com.example.bustamante.utils.ReadFIle

class ProductHeaderAdapter(private val proveedor: Proveedor) :
    RecyclerView.Adapter<ProductHeaderAdapter.HeaderViewHolder>() {

    private var updateProductList: ((SpannableString) -> Unit)? = null

    inner class HeaderViewHolder(private val binding: ProductHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.tipo.text = proveedor.tipo
            binding.nombre.text = proveedor.nombre
            configDownloadBtn(proveedor, binding)
            updateProductList = {
                binding.productList.text = it
            }
        }

    }

    private fun configDownloadBtn(proveedor: Proveedor, binding: ProductHeaderBinding) {
        if (proveedor.informacion.isNullOrEmpty()) return

        binding.downloadBtn.visibility = View.VISIBLE
        val keys = proveedor.informacion.keys.toList()

        binding.downloadBtn.setOnClickListener {
            val popupMenu = PopupMenu(binding.root.context, it)
            keys.indices.map { index -> popupMenu.menu.add(0, index, index, keys[index]) }

            popupMenu.setOnMenuItemClickListener { menuItem ->
                val url = proveedor.informacion[menuItem.title]
                url ?: return@setOnMenuItemClickListener false
                ReadFIle().openDocument(binding.root.context, url)
                true
            }
            popupMenu.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProductHeaderBinding.inflate(inflater, parent, false)
        return HeaderViewHolder(binding)
    }

    override fun getItemCount(): Int = 1
    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind()
    }

    fun setProductList(spannableString: SpannableString) {
        updateProductList?.invoke(spannableString)
    }
}