package com.hallen.bustamante.service

import android.content.Context
import com.hallen.bustamante.domain.ProductUseCase
import com.orhanobut.logger.Logger

class ProductUpdater(context: Context, private val productUseCase: ProductUseCase) :
    Updater(context) {
    suspend fun updateProductosIfNeeded(): Boolean? {
        val hasToUpdate = productUseCase.hasToUpdate("productos") ?: return null
        Logger.i("Hay que actualizar los productos: $hasToUpdate")
        return if (!hasToUpdate) false else updateProductos()
    }

    private suspend fun updateProductos(): Boolean {
        val productos = productUseCase.getAllProductsFromApi()

        return if (productos.isEmpty()) false else {
            productos.map {
                intentosMaximos = 0
                it.imagen = storeFileAndGetPath(it.imagen, "imagenes") ?: return false
            }
            productUseCase.insertProducts(productos)
            true
        }
    }
}
