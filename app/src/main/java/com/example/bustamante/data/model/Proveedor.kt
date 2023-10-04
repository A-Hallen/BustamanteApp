package com.example.bustamante.data.model

import com.google.gson.Gson

data class Proveedor(
    val proveedorId: String,
    val informacion: Map<String, String>?,
    val nombre: String,
    val tipo: String,
    val productos: List<ProveedorProductResponse>?
)

fun Proveedor.informacionToDatabase(): String {
    val gson = Gson()
    if (informacion == null) return "{}"
    return gson.toJson(informacion)
}

fun Proveedor.productosToDatabase(): String {
    val gson = Gson()
    if (productos == null) return "{}"
    return gson.toJson(productos)
}