package com.hallen.bustamante.network

import com.hallen.bustamante.data.model.Product
import retrofit2.Response
import retrofit2.http.GET

interface ProductApiClient {
    @GET("/productos-list")
    suspend fun getAllProducts(): Response<List<Product>>
}
