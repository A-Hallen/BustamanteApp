package com.hallen.bustamante.ui.tcps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallen.bustamante.R
import com.hallen.bustamante.data.model.Proveedor
import com.hallen.bustamante.databinding.FragmentProductosBinding
import com.hallen.bustamante.ui.recyclers.adapters.TcpAdapter
import com.google.android.material.transition.Hold
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TcpsFragment : Fragment() {
    private val proveedorViewModel: ProveedorViewModel by activityViewModels()
    private val adapter by lazy { TcpAdapter() }
    private lateinit var binding: FragmentProductosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        exitTransition = Hold()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // this is required to animate correctly when the user returns
        // to the origin fragment, gives a chance for the layout
        // to be fully laid out before animating it
        postponeEnterTransition()
        super.onViewCreated(view, savedInstanceState)

        getObservableList(arguments).observe(viewLifecycleOwner) {
            adapter.update(it)
            binding.recyclerView.doOnPreDraw {
                startPostponedEnterTransition()
            }
        }
        setupRecyclerView()
        proveedorViewModel.getProveedores()
    }

    private val cardClicked = fun(item: Proveedor, card: ConstraintLayout) {
        card.transitionName = "proveedor_transition"
        val extras = FragmentNavigator
            .Extras
            .Builder()
            .addSharedElement(card, "proveedor_transition")
            .build()

        val gson = Gson().toJson(item)
        val bundle = Bundle()
        bundle.putString("proveedor", gson)
        val navHostFragment = NavHostFragment.findNavController(this)
        val destination = navHostFragment.graph.findNode(R.id.proveedor_full_size)
        destination?.label = item.nombre
        navHostFragment.navigate(R.id.action_nav_tcps_to_proveedor_full_size, bundle, null, extras)
    }

    private fun getObservableList(arguments: Bundle?): MutableLiveData<List<Proveedor>> {
        return if (arguments?.getString("tipo") == "TCP") {
            proveedorViewModel.tcpList
        } else proveedorViewModel.mypimeList
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager
        adapter.setCardClicked(cardClicked)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
    }
}