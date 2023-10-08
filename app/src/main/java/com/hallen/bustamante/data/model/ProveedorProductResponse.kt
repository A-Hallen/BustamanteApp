package com.hallen.bustamante.data.model

data class ProveedorProductResponse(
    val cantidad: String,
    val categoria: String,
    val color: String,
    val descripcion: String,
    var imagen: String,
    val nombre: String,
    val precio: String,
    val proveedorId: String,
    val tabla: Map<String, String>?
)