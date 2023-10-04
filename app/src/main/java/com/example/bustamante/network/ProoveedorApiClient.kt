package com.example.bustamante.network

import com.example.bustamante.data.model.Proveedor
import retrofit2.Response
import retrofit2.http.GET

interface ProoveedorApiClient {
    @GET("/proveedores-list")
    suspend fun getAllProveedores(): Response<List<Proveedor>>

}
