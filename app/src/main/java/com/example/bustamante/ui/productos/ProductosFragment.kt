package com.example.bustamante.ui.productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
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
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter()
        // Removes blinks
        // Removes blinks
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
    }

}