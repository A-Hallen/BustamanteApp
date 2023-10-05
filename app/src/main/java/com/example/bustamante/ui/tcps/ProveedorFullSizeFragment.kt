package com.example.bustamante.ui.tcps

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.bustamante.R
import com.example.bustamante.data.model.Product
import com.example.bustamante.data.model.Proveedor
import com.example.bustamante.databinding.FragmentProveedorFullSizeBinding
import com.example.bustamante.domain.ProductUseCase
import com.example.bustamante.ui.recyclers.adapters.ProductAdapter
import com.example.bustamante.utils.ReadFIle
import com.google.android.material.transition.MaterialContainerTransform
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProveedorFullSizeFragment : Fragment() {

    @Inject
    lateinit var productUseCase: ProductUseCase
    private val produtosLiveData = MutableLiveData<List<Product>>()

    private lateinit var binding: FragmentProveedorFullSizeBinding
    private var jsonItem: String? = null
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        jsonItem = arguments?.getString("proveedor")
        sharedElementEnterTransition = MaterialContainerTransform()
        postponeEnterTransition()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProveedorFullSizeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        jsonItem ?: return
        val proveedor = Gson().fromJson(jsonItem, Proveedor::class.java)
        configViews(proveedor)
        binding.tipo.setOnClickListener {
            childFragmentManager.popBackStack()
        }
    }

    private fun configViews(proveedor: Proveedor) {
        proveedor.productos ?: return
        setupRecyclerView(proveedor.proveedorId)
        binding.tipo.text = proveedor.tipo
        binding.nombre.text = proveedor.nombre
        configDownloadBtn(proveedor)
    }

    private fun configDownloadBtn(proveedor: Proveedor) {
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

    private val cardClicked = fun(item: Product, card: ConstraintLayout) {
        card.transitionName = "product_transition"
        val extras = FragmentNavigator
            .Extras
            .Builder()
            .addSharedElement(card, "product_transition")
            .build()

        val bundle = Bundle()
        bundle.putString("imagen", item.imagen)
        val navHostFragment = NavHostFragment.findNavController(this)
        val destination = navHostFragment.graph.findNode(R.id.product_full_size)
        destination?.label = item.nombre
        navHostFragment.navigate(
            R.id.action_proveedor_full_size_to_product_full_size, bundle, null, extras
        )
    }

    private fun setupRecyclerView(id: String) {
        produtosLiveData.observe(viewLifecycleOwner) {
            val productNames: String = it.joinToString(", ") { product -> product.nombre }
            val spannableString = SpannableString("Productos: $productNames")
            val estiloNegrita = StyleSpan(Typeface.BOLD)
            spannableString.setSpan(estiloNegrita, 0, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.productList.text = spannableString

            adapter.update(it)
        }

        adapter = ProductAdapter()
        adapter.setCardClicked(cardClicked)
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.doOnPreDraw {
            startPostponedEnterTransition()
        }

        CoroutineScope(Dispatchers.IO).launch {
            val productos = productUseCase.getProductsFromProviderId(id)
            produtosLiveData.postValue(productos)
        }
    }

}