package com.hallen.bustamante.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger

@Entity(tableName = "proveedor_table")
data class ProveedorEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "proveedorId") var proveedorId: String = "",
    @ColumnInfo(name = "informacion") var informacion: String = "",
    @ColumnInfo(name = "nombre") var nombre: String = "",
    @ColumnInfo(name = "tipo") var tipo: String = "",
    @ColumnInfo(name = "productos") var productos: String = "",
)

fun ProveedorEntity.toDomain() = Proveedor(
    proveedorId, getInformacion(), nombre, tipo, getProductos()
)

private fun ProveedorEntity.getProductos(): List<Product> {
    // Convertir el String a lista de pares
    val gson = Gson()
    val tipo = object : TypeToken<List<Product>>() {}.type
    return gson.fromJson(productos, tipo)
}

private fun ProveedorEntity.getInformacion(): List<Information> {
    // Convertir el String a lista de pares
    val gson = Gson()
    val tipo = object : TypeToken<List<Information>>() {}.type
    Logger.i("ERROR: $informacion")
    return gson.fromJson(informacion, tipo)
}

fun Proveedor.toDataBase() = ProveedorEntity(
    proveedorId, informacionToDatabase(), nombre, tipo, productosToDatabase()
)


//data class Proveedor(
//    val proveedorId: String,
//    val informacion: Map<String, String>,
//    val nombre: String,
//    val tipo: String,
//    val productos: List<Map<String, String>>
//)