package com.example.bustamante.network

import com.example.bustamante.data.model.Product
import retrofit2.Response
import retrofit2.http.GET

interface ProductApiClient {
    @GET("/productos-list")
    suspend fun getAllProducts(): Response<List<Product>>
}