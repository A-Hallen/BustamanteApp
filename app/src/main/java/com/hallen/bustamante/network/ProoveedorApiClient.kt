package com.hallen.bustamante.network

import com.hallen.bustamante.data.model.Proveedor
import retrofit2.Response
import retrofit2.http.GET

interface ProoveedorApiClient {
    @GET("/proveedores-list")
    suspend fun getAllProveedores(): Response<List<Proveedor>>
}
