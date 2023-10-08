package com.hallen.bustamante.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProveedorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(proveedorEntity: ProveedorEntity)

    @Query("SELECT * FROM proveedor_table ORDER By nombre ASC")
    suspend fun getAllProveedores(): List<ProveedorEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProveedores(map: List<ProveedorEntity>)
}
