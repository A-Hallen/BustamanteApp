package com.hallen.bustamante.data.model

import com.google.gson.Gson

data class Proveedor(
    val proveedorId: String,
    val informacion: List<Information>?,
    val nombre: String,
    val tipo: String,
    val productos: List<Product>?
)

fun Proveedor.informacionToDatabase(): String {
    val gson = Gson()
    if (informacion == null) return "[]"
    return gson.toJson(informacion)
}

fun Proveedor.productosToDatabase(): String {
    val gson = Gson()
    if (productos == null) return "{}"
    return gson.toJson(productos)
}
