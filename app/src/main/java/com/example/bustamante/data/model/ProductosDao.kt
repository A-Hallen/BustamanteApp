package com.example.bustamante.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductosDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(productosEntity: ProductosEntity)

    @Query("SELECT * FROM productos_table ORDER BY nombre ASC")
    suspend fun getAllProducts(): List<ProductosEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(map: List<ProductosEntity>)
}