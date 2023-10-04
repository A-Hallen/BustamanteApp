package com.example.bustamante.network

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductUpdateApi {
    @GET("/update-date")
    suspend fun getLastUpdateDate(@Query("fecha") tipoFecha: String): Response<JsonObject>
}
