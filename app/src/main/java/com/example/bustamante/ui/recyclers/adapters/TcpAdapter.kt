package com.example.bustamante.ui.recyclers.adapters

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bustamante.data.model.Proveedor
import com.example.bustamante.data.model.ProveedorProductResponse
import com.example.bustamante.databinding.ItemTcpBinding
import com.example.bustamante.databinding.ProductImageViewBinding
import com.example.bustamante.ui.recyclers.diffs.TcpDiffCallback
import com.example.bustamante.utils.ReadFIle
import com.orhanobut.logger.Logger
import java.io.File

class TcpAdapter : RecyclerView.Adapter<TcpAdapter.TcpViewHolder>() {
    private var proveedoresList: List<Proveedor> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TcpViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTcpBinding.inflate(layoutInflater, parent, false)
        return TcpViewHolder(binding)
    }

    override fun getItemCount(): Int = proveedoresList.size

    override fun onBindViewHolder(holder: TcpViewHolder, position: Int) {
        holder.bind(position)
    }

    fun update(it: List<Proveedor>) {
        val tcpDiffCallback = TcpDiffCallback(proveedoresList, it)
        val diffResult = DiffUtil.calculateDiff(tcpDiffCallback)
        proveedoresList = it
        diffResult.dispatchUpdatesTo(this)
    }

    inner class TcpViewHolder(private val binding: ItemTcpBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val item = proveedoresList[position]
            binding.nombre.text = item.nombre
            binding.tipo.text = item.tipo

            binding.downloadBtn.visibility =
                if (item.informacion == null) View.GONE else View.VISIBLE

            configInformation(binding, item.informacion)

            val visibility = if (item.productos == null) View.GONE else View.VISIBLE
            binding.images.visibility = visibility
            binding.productList.visibility = visibility
            item.productos ?: return

            agregaImagenes(item.productos, binding.images)
            val productNames: String = item.productos.joinToString(", ") { it.nombre }
            val spannableString = SpannableString("Productos: $productNames")
            val estiloNegrita = StyleSpan(Typeface.BOLD)
            spannableString.setSpan(estiloNegrita, 0, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.productList.text = spannableString
        }
    }

    private fun configInformation(binding: ItemTcpBinding, informacion: Map<String, String>?) {
        val keys = informacion?.keys?.toList() ?: return

        binding.downloadBtn.setOnClickListener {
            val popupMenu = PopupMenu(binding.root.context, it)
            keys.indices.map { index -> popupMenu.menu.add(0, index, index, keys[index]) }

            popupMenu.setOnMenuItemClickListener { menuItem ->
                Logger.i("Information: ${informacion[menuItem.title]}, title: ${menuItem.title}")
                ReadFIle().openDocument(
                    binding.root.context, informacion[menuItem.title] ?: ""
                )
                true
            }
            popupMenu.show()
        }
    }

    private fun agregaImagenes(
        productos: List<ProveedorProductResponse>,
        linearLayout: LinearLayout
    ) {
        val inflater = LayoutInflater.from(linearLayout.context)

        for (producto in productos) {
            val src = producto.imagen
            val color = producto.color
            val imageView = ProductImageViewBinding.inflate(inflater, linearLayout, false).root
            linearLayout.addView(imageView)
            val imagePath = File("${linearLayout.context.filesDir}/imagenes", src)
            Glide.with(linearLayout.context).load(imagePath)
                .placeholder(ColorDrawable(Color.parseColor(color)))
                .into(imageView)
        }
    }

}
