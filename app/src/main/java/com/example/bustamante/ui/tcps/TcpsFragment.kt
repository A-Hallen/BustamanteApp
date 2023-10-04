package com.example.bustamante.ui.tcps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bustamante.data.model.Proveedor
import com.example.bustamante.databinding.FragmentProductosBinding
import com.example.bustamante.ui.recyclers.adapters.TcpAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TcpsFragment : Fragment() {
    private val proveedorViewModel: ProveedorViewModel by activityViewModels()
    private val adapter by lazy { TcpAdapter() }
    private lateinit var binding: FragmentProductosBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getObservableList(arguments).observe(viewLifecycleOwner) {
            adapter.update(it)
        }
        setupRecyclerView()
        proveedorViewModel.getProveedores()
    }

    private fun getObservableList(arguments: Bundle?): MutableLiveData<List<Proveedor>> {
        return if (arguments?.getString("tipo") == "TCP") {
            proveedorViewModel.tcpList
        } else proveedorViewModel.mypimeList
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
    }
}