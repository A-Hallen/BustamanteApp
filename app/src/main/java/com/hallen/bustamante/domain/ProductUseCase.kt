package com.hallen.bustamante.domain

import com.hallen.bustamante.data.model.DateDao
import com.hallen.bustamante.data.model.DateEntity
import com.hallen.bustamante.data.model.Product
import com.hallen.bustamante.data.model.ProductosDao
import com.hallen.bustamante.data.model.toDataBase
import com.hallen.bustamante.data.model.toDomain
import com.hallen.bustamante.network.ProductoService
import com.orhanobut.logger.Logger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ProductUseCase @Inject constructor(
    private val productosDao: ProductosDao,
    private val dateDao: DateDao
) {

    private val formato: SimpleDateFormat = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        Locale.US
    )

    suspend fun getAllProducts(): List<Product> =
        productosDao.getAllProducts().map { it.toDomain() }

    suspend fun insert(proucto: Product) {
        productosDao.insert(proucto.toDataBase())
    }

    suspend fun getAllProductsFromApi(): List<Product> = ProductoService().getAllProducts()

    suspend fun insertProducts(productos: List<Product>) {
        productosDao.insertProducts(productos.map { it.toDataBase() })
    }

    fun insertUpdateTime(date: Date) {
        val entity = DateEntity("productos", date)
        dateDao.insert(entity)
    }

    suspend fun hasToUpdate(id: String): Boolean? {
        val serverDateString = ProductoService().getLastUpdateDate()
        val serverDate = if (serverDateString.isNotBlank()) {
            formato.parse(serverDateString)
        } else return null
        val localDate = dateDao.getLastUpdateDate(id)
        Logger.i("localDate: $localDate, serverDate: $serverDate")
        return serverDate != null && serverDate > localDate
    }

    suspend fun getProductsFromProviderId(id: String): List<Product> =
        productosDao.getProductsFromProviderId(id).map { it.toDomain() }
}

