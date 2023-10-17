package com.hallen.bustamante.service

import android.content.Context
import com.hallen.bustamante.data.model.Information
import com.hallen.bustamante.data.model.Product
import com.hallen.bustamante.data.model.Proveedor
import com.hallen.bustamante.domain.ProductUseCase
import com.hallen.bustamante.domain.ProveedorUseCase
import com.orhanobut.logger.Logger

class ProveedorUpdater(context: Context, private val proveedorUseCase: ProveedorUseCase) :
    Updater(context) {

    suspend fun updateProveedoresIfNeeded(productUseCase: ProductUseCase): Boolean? {
        val hasToUpdate = productUseCase.hasToUpdate("proveedores") ?: return null
        Logger.i("Hay que actualizar los proveedores: $hasToUpdate")
        return if (!hasToUpdate) false else updateProveedores()
    }

    private suspend fun updateProveedores(): Boolean {
        val proveedores = proveedorUseCase.getAllProveedorFromApi()
        val proveedoresWithLocalImageUrls: List<Proveedor> = proveedores.map { proveedor ->
            val newInformation = getInformationWithLocalUrls(proveedor)
            val newProductos = getProductosWithLocalUrls(proveedor)
            if (newInformation == null || newProductos == null) return false
            proveedor.copy(informacion = newInformation, productos = newProductos)
        }
        proveedorUseCase.insertProveedores(proveedoresWithLocalImageUrls)
        return true
    }

    private fun getProductosWithLocalUrls(proveedor: Proveedor): List<Product>? {
        return proveedor.productos?.map {
            intentosMaximos = 0
            val imagen = storeFileAndGetPath(it.imagen, "imagenes") ?: return null
            it.copy(imagen = imagen)
        } ?: emptyList()
    }

    private fun getInformationWithLocalUrls(proveedor: Proveedor): List<Information>? {
        return proveedor.informacion?.map {
            intentosMaximos = 0
            val url =
                storeFileAndGetPath(
                    it.url,
                    "documentos",
                    "${it.nombre}.${it.extension}",
                    it.hash
                ) ?: return null
            Information(
                nombre = it.nombre,
                hash = it.hash,
                url = url,
                extension = it.extension
            )
        } ?: emptyList()
    }

}
