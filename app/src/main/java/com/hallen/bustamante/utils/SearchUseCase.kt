package com.hallen.bustamante.utils

import android.widget.SearchView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.hallen.bustamante.R
import com.hallen.bustamante.data.model.Product
import com.hallen.bustamante.data.model.Proveedor
import com.hallen.bustamante.ui.productos.ProductosViewModel
import com.hallen.bustamante.ui.tcps.ProveedorViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SEARCH_DELAY_MILLIS = 100L

class SearchUseCase(
    private val navController: NavController,
    private val productosViewModel: ProductosViewModel,
    private val proveedorViewModel: ProveedorViewModel
) {
    private var productList = listOf<Product>()
    private var mypimeList = listOf<Proveedor>()
    private var tcpList = listOf<Proveedor>()

    init {
        productosViewModel.productosList.observeOnce { productList = it }
        proveedorViewModel.mypimeList.observeOnce { mypimeList = it }
        proveedorViewModel.tcpList.observeOnce { tcpList = it }
    }

    fun searchViewClick(searchView: SearchView) {
        var searchJob: Job? = null
        val searchListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    filterQyeryByDestination(newText)
                }
                return false
            }
        }
        searchView.setOnQueryTextListener(searchListener)
    }

    private suspend fun filterQyeryByDestination(newText: String?) {
        delay(SEARCH_DELAY_MILLIS)
        when (navController.currentDestination?.id) {
            R.id.nav_productos -> {
                val newValue = productList.filter {
                    it.nombre.contains(newText.orEmpty(), ignoreCase = true)
                }
                productosViewModel.productosList.postValue(newValue)
            }

            R.id.nav_mypimes -> {
                val newValue = mypimeList.filter {
                    it.nombre.contains(newText.orEmpty(), ignoreCase = true)
                }
                proveedorViewModel.mypimeList.postValue(newValue)
            }

            R.id.nav_tcps -> {
                val newValue = tcpList.filter {
                    it.nombre.contains(newText.orEmpty(), ignoreCase = true)
                }
                proveedorViewModel.tcpList.postValue(newValue)
            }
        }
    }
}

private fun <T> MutableLiveData<T>.observeOnce(observer: Observer<T>) {
    observeForever(object : Observer<T> {
        override fun onChanged(value: T) {
            observer.onChanged(value)
            removeObserver(this)
        }
    })
}
