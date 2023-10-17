package com.hallen.bustamante.ui.recyclers.adapters

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
import com.hallen.bustamante.data.model.Information
import com.hallen.bustamante.data.model.Product
import com.hallen.bustamante.data.model.Proveedor
import com.hallen.bustamante.databinding.ItemTcpBinding
import com.hallen.bustamante.databinding.ProductImageViewBinding
import com.hallen.bustamante.ui.recyclers.diffs.TcpDiffCallback
import com.hallen.bustamante.utils.ReadFIle
import java.io.File

class TcpAdapter : RecyclerView.Adapter<TcpAdapter.TcpViewHolder>() {
    private var proveedoresList: List<Proveedor> = listOf()
    private lateinit var cardClicked: (Proveedor, View) -> Unit

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
                if (item.informacion.isNullOrEmpty()) View.GONE else View.VISIBLE

            item.informacion?.let { configInformation(binding, it) }
            configCard(binding, item)

            val visibility = if (item.productos == null) View.GONE else View.VISIBLE
            binding.images.visibility = visibility
            binding.productList.visibility = visibility
            item.productos ?: return

            agregaImagenes(item.productos, binding.images)
            val productNames: String = item.productos.joinToString(", ") { it.nombre }
            val spannableString = SpannableString("Productos: $productNames")
            val estiloNegrita = StyleSpan(Typeface.BOLD)
            spannableString.setSpan(
                estiloNegrita,
                0,
                "Productos: ".length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.productList.text = spannableString
        }
    }

    private fun configCard(binding: ItemTcpBinding, item: Proveedor) {
        binding.card.setOnClickListener {
            cardClicked(item, binding.card)
        }
    }

    private fun configInformation(binding: ItemTcpBinding, informacion: List<Information>) {
        binding.downloadBtn.setOnClickListener {
            val popupMenu = PopupMenu(binding.root.context, it)

            informacion.forEachIndexed { index, information ->
                popupMenu.menu.add(0, index, index, information.nombre)
            }

            popupMenu.setOnMenuItemClickListener { menuItem ->
                ReadFIle().openDocument(
                    binding.root.context, informacion[menuItem.itemId].url
                )
                true
            }
            popupMenu.show()
        }
    }

    private fun agregaImagenes(
        productos: List<Product>,
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

    fun setCardClicked(cardClicked: (Proveedor, View) -> Unit) {
        this.cardClicked = cardClicked
    }

}
