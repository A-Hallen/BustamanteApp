package com.example.bustamante.ui.productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.bustamante.databinding.FragmentProductFullSizeBinding
import com.google.android.material.transition.MaterialFade
import java.io.File


class ProductFullSizeFragment : Fragment() {
    private lateinit var binding: FragmentProductFullSizeBinding

    private var imagen: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagen = arguments?.getString("imagen")
        sharedElementEnterTransition = MaterialFade()
        postponeEnterTransition()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductFullSizeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagen ?: return
        val file = File("${binding.root.context.filesDir}/imagenes", imagen!!)
        postponeEnterTransition()
        Glide.with(this)
            .load(file)
            .into(binding.root)
        binding.root.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

}