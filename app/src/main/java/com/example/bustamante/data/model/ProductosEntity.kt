package com.example.bustamante.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "productos_table")
data class ProductosEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id") var id: String = "",
    @ColumnInfo(name = "cantidad") var cantidad: String = "",
    @ColumnInfo(name = "categoria") var categoria: String = "",
    @ColumnInfo(name = "color") var color: String = "",
    @ColumnInfo(name = "descripcion") var descripcion: String = "",
    @ColumnInfo(name = "imagen") var imagen: String = "",
    @ColumnInfo(name = "nombre") var nombre: String = "",
    @ColumnInfo(name = "precio") var precio: String = "",
    @ColumnInfo(name = "proveedor_id") var proveedorId: String = "",
    @ColumnInfo(name = "tabla") var tabla: String,
    @ColumnInfo(name = "nombreProveedor") var nombreProveedor: String,
    @ColumnInfo(name = "tipoProveedor") var tipoProveedor: String,
)

fun ProductosEntity.toDomain() = Product(
    id, cantidad, categoria, color, descripcion,
    imagen, nombre, precio, proveedorId, getDatos(),
    nombreProveedor, tipoProveedor
)

private fun ProductosEntity.getDatos(): Map<String, String> {
    // Convertir el String a lista de pares
    val gson = Gson()
    val tipo = object : TypeToken<Map<String, String>>() {}.type
    return gson.fromJson(tabla, tipo)
}

fun Product.toDataBase() = ProductosEntity(
    id, cantidad, categoria, color, descripcion,
    imagen, nombre, precio, proveedorId, datosToString(),
    nombreProveedor, tipoProveedor
)
