package com.example.bustamante.data.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Product(
    val id: String,
    val cantidad: String,
    val categoria: String,
    val color: String,
    val descripcion: String,
    var imagen: String,
    val nombre: String,
    val precio: String,
    val proveedorId: String,
    val tabla: Map<String, String>?,
    val nombreProveedor: String,
    val tipoProveedor: String,
) {
    // Convertir la lista de pares a String
    fun datosToString(): String {
        val gson = Gson()
        if (tabla == null) return "{}"
        return gson.toJson(tabla)
    }

    // Convertir el String a lista de pares
    fun stringToDatos(datosString: String): List<Pair<String, String>> {
        val gson = Gson()
        val tipo = object : TypeToken<List<Pair<String, String>>>() {}.type
        return gson.fromJson(datosString, tipo)
    }
}
