package com.hallen.bustamante.ui.tcps

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.transition.MaterialContainerTransform
import com.google.gson.Gson
import com.hallen.bustamante.R
import com.hallen.bustamante.data.model.Product
import com.hallen.bustamante.data.model.Proveedor
import com.hallen.bustamante.databinding.FragmentProveedorFullSizeBinding
import com.hallen.bustamante.domain.ProductUseCase
import com.hallen.bustamante.ui.recyclers.adapters.ProductAdapter
import com.hallen.bustamante.ui.recyclers.adapters.ProductHeaderAdapter
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
    private lateinit var headerAdapter: ProductHeaderAdapter

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
    }

    private fun configViews(proveedor: Proveedor) {
        proveedor.productos ?: return
        setupHeader(proveedor)
        setupRecyclerView(proveedor.proveedorId)
    }

    private fun setupHeader(proveedor: Proveedor) {
        headerAdapter = ProductHeaderAdapter(proveedor)
    }

    private val cardClicked = fun(item: Product, view: ImageView) {
        view.transitionName = "product_transition"
        val extras = FragmentNavigator
            .Extras
            .Builder()
            .addSharedElement(view, "product_transition")
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
            spannableString.setSpan(
                estiloNegrita,
                0,
                "Productos: ".length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            headerAdapter.notifyItemChanged(0)
            headerAdapter.setProductList(spannableString)
            adapter.update(it)
        }

        adapter = ProductAdapter()
        adapter.setCardClicked(cardClicked)
        val concatAdapter = ConcatAdapter(headerAdapter, adapter)
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = concatAdapter
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

