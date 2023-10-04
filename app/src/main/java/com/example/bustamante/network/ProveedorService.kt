package com.example.bustamante.network

import com.example.bustamante.core.RetrofitHelper
import com.example.bustamante.data.model.Proveedor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProveedorService {
    private val retrofit = RetrofitHelper.getRetrofit()

    suspend fun getAllProveedores(): List<Proveedor> {
        return withContext(Dispatchers.IO) {
            val api = retrofit.create(ProoveedorApiClient::class.java)
            val response = api.getAllProveedores()
            response.body() ?: emptyList()
        }
    }

}
