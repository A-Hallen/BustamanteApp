package com.example.bustamante.domain

import com.example.bustamante.data.model.DateDao
import com.example.bustamante.data.model.DateEntity
import com.example.bustamante.data.model.Proveedor
import com.example.bustamante.data.model.ProveedorDao
import com.example.bustamante.data.model.toDataBase
import com.example.bustamante.data.model.toDomain
import com.example.bustamante.network.ProveedorService
import java.util.Date
import javax.inject.Inject

class ProveedorUseCase @Inject constructor(
    private val proveedorDao: ProveedorDao,
    private val dateDao: DateDao
) {
    suspend fun getAllProveedorFromApi(): List<Proveedor> = ProveedorService().getAllProveedores()

    suspend fun insertProveedores(proveedores: List<Proveedor>) {
        proveedorDao.insertProveedores(proveedores.map { it.toDataBase() })
        insertUpdateTime(Date())
    }

    suspend fun getAllProveedores(): List<Proveedor> =
        proveedorDao.getAllProveedores().map { it.toDomain() }


    private fun insertUpdateTime(date: Date) {
        val entity = DateEntity("proveedores", date)
        dateDao.insert(entity)
    }
}