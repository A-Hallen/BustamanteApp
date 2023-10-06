package com.example.bustamante.ui.productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.bustamante.R
import com.example.bustamante.data.model.Product
import com.example.bustamante.databinding.FragmentProductosBinding
import com.example.bustamante.ui.recyclers.adapters.ProductAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProductosFragment : Fragment() {
    private val productosViewModel: ProductosViewModel by activityViewModels()
    private lateinit var adapter: ProductAdapter
    private lateinit var binding: FragmentProductosBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductosBinding.inflate(inflater, container, false)
        //viewModel.getProductos()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productosViewModel.productosList.observe(viewLifecycleOwner) {
            adapter.update(it)
        }
        productosViewModel.getProductos()
        postponeEnterTransition()
        binding.recyclerView.doOnPreDraw {
            startPostponedEnterTransition()
        }
        setupRecyclerView()
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
            R.id.action_nav_productos_to_product_full_size,
            bundle,
            null,
            extras
        )
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter()
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        adapter.setCardClicked(cardClicked)
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
    }

}