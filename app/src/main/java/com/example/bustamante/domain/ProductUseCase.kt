package com.example.bustamante.domain

import com.example.bustamante.data.model.DateDao
import com.example.bustamante.data.model.DateEntity
import com.example.bustamante.data.model.Product
import com.example.bustamante.data.model.ProductosDao
import com.example.bustamante.data.model.toDataBase
import com.example.bustamante.data.model.toDomain
import com.example.bustamante.network.ProductoService
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

    suspend fun insertUpdateTime(date: Date) {
        val entity = DateEntity("productos", date)
        dateDao.insert(entity)
    }

    suspend fun hasToUpdate(id: String): Boolean? {
        val serverDateString = ProductoService().getLastUpdateDate()
        if (serverDateString.isEmpty()) return null
        val localDate = dateDao.getLastUpdateDate(id) ?: return true
        val serverDate = formato.parse(serverDateString) ?: return null
        Logger.i("localDate: $localDate, serverDate: $serverDate")
        return serverDate > localDate
    }

    suspend fun getProductsFromProviderId(id: String): List<Product> =
        productosDao.getProductsFromProviderId(id).map { it.toDomain() }
}