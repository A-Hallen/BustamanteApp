package com.example.bustamante.ui.recyclers.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bustamante.R
import com.example.bustamante.data.model.Product
import com.example.bustamante.databinding.ItemProductBinding
import com.example.bustamante.ui.recyclers.diffs.ProductDiffCallback
import java.io.File


class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private var products: List<Product> = listOf()
    private lateinit var cardClicked: (Product, ImageView) -> Unit


    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val item = products[position]

            val imagePath = File("${binding.root.context.filesDir}/imagenes", item.imagen)
            Glide.with(binding.root.context).load(imagePath).into(binding.imagen)
            binding.nombre.text = item.nombre
            binding.cantidad.text = item.cantidad
            binding.proveedor.text = item.nombreProveedor

            configCard(binding, item)


            binding.tableList.visibility = View.GONE
            if (item.tabla.isNullOrEmpty()) {
                binding.precio.text = item.precio
                binding.precio.visibility = View.VISIBLE
                binding.textPrecios.visibility = View.GONE
                return
            }

            binding.precio.visibility = View.GONE
            binding.textPrecios.visibility = View.VISIBLE
            binding.textPrecios.setOnClickListener {
                if (binding.tableList.visibility == View.VISIBLE) {
                    binding.tableList.visibility = View.GONE
                    binding.textPrecios.text = "Mostrar Precios"
                    return@setOnClickListener
                }
                binding.textPrecios.text = "Ocultar Precios"
                addTableData(item.tabla, binding.tableList)
                binding.tableList.visibility = View.VISIBLE
            }
        }
    }

    private fun configCard(binding: ItemProductBinding, item: Product) {
        binding.card.setOnClickListener {
            cardClicked(item, binding.imagen)
        }
    }

    private fun setListViewHeightBasedOnChildren(listView: ListView) {
        listView.post(Runnable {
            val listAdapter = listView.adapter ?: return@Runnable
            var totalHeight = listView.paddingTop + listView.paddingBottom
            val listWidth = listView.measuredWidth
            for (i in 0 until listAdapter.count) {
                val listItem = listAdapter.getView(i, null, listView)
                listItem.measure(
                    View.MeasureSpec.makeMeasureSpec(listWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                totalHeight += listItem.measuredHeight
                Log.d("listItemHeight " + listItem.measuredHeight, "********")
            }
            Log.d("totalHeight $totalHeight", "********")
            val params = listView.layoutParams
            params.height = totalHeight + listView.dividerHeight * (listAdapter.count)
            listView.layoutParams = params
            listView.requestLayout()
        })
    }

    private fun addTableData(tableData: Map<String, String>, tableList: ListView) {
        val adapter = TableAdapter(tableData)
        tableList.adapter = adapter
        setListViewHeightBasedOnChildren(tableList)
    }

    inner class TableAdapter(private val tableData: Map<String, String>) : BaseAdapter() {

        private val keys: List<String> = tableData.keys.toList()

        override fun getCount(): Int = tableData.size

        override fun getItem(p0: Int): Any {
            val key = keys[p0]
            return Pair(key, tableData[key])
        }

        override fun getItemId(p0: Int): Long = p0.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View
            val viewHolder: ViewHolder

            if (convertView == null) {
                val inflater = LayoutInflater.from(parent?.context)
                view = inflater.inflate(R.layout.item_tabla, parent, false)
                viewHolder = ViewHolder(view)
                view.tag = viewHolder
            } else {
                view = convertView
                viewHolder = view.tag as ViewHolder
            }

            val key = keys[position]
            val value = tableData[key]

            viewHolder.productoTv.text = key
            viewHolder.precioTv.text = value

            return view
        }

        private inner class ViewHolder(view: View) {
            val productoTv: TextView = view.findViewById(R.id.tabla_producto)
            val precioTv: TextView = view.findViewById(R.id.tabla_precio)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemProductBinding.inflate(layoutInflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(position)
    }

    fun update(it: List<Product>) {
        val productDiffCallback = ProductDiffCallback(products, it)
        val diffResult = DiffUtil.calculateDiff(productDiffCallback)
        products = it
        diffResult.dispatchUpdatesTo(this)
    }

    fun setCardClicked(cardClicked: (Product, ImageView) -> Unit) {
        this.cardClicked = cardClicked
    }
}