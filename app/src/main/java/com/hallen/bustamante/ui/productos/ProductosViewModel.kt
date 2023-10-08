package com.hallen.bustamante.ui.productos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hallen.bustamante.data.model.Product
import com.hallen.bustamante.domain.ProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductosViewModel @Inject constructor(
    private val productosUseCase: ProductUseCase
) : ViewModel() {

    val productosList = MutableLiveData<List<Product>>()

    fun getProductos() {
        CoroutineScope(Dispatchers.IO).launch {
            val productos = productosUseCase.getAllProducts()
            productosList.postValue(productos)
        }
    }

    suspend fun getProductsFromProviderId(id: String): List<Product> {
        return productosUseCase.getProductsFromProviderId(id)
    }

//    fun getProductosFromApi() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val productos = productosUseCase.getAllProductsFromApi()
//            productosList.postValue(productos)
//        }
//    }
//
//    fun insertProducts(proucto: Product) {
//        CoroutineScope(Dispatchers.IO).launch {
//            productosUseCase.insert(proucto)
//        }
//    }

}