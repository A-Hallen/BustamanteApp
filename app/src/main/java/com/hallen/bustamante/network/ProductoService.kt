package com.hallen.bustamante.network

import com.hallen.bustamante.core.RetrofitHelper
import com.hallen.bustamante.data.model.Product
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.EOFException

class ProductoService {
    private val retrofit = RetrofitHelper.getRetrofit()

    @Suppress("TooGenericExceptionCaught")
    suspend fun getAllProducts(): List<Product> {
        return withContext(Dispatchers.IO) {
            val api = retrofit.create(ProductApiClient::class.java)
            try {
                val response = api.getAllProducts()
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Logger.e(e.cause, e.message.toString())
                // Manejo de errores en caso de excepción
            }
            emptyList()
        }
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun getLastUpdateDate(): String {
        return withContext(Dispatchers.IO) {
            val api = retrofit.create(ProductUpdateApi::class.java)
            try {
                val response = api.getLastUpdateDate("productos")
                if (response.isSuccessful) {
                    val fecha = response.body()?.get("fecha")?.asString
                    fecha ?: ""
                }
            } catch (e: EOFException) {
                Logger.e(e.cause, e.message.toString())
            } catch (e: Exception) {
                Logger.e(e.cause, e.message.toString())
            }
            ""
        }
    }
}
