package com.hallen.bustamante.domain

import com.hallen.bustamante.data.model.DateDao
import com.hallen.bustamante.data.model.DateEntity
import com.hallen.bustamante.data.model.Proveedor
import com.hallen.bustamante.data.model.ProveedorDao
import com.hallen.bustamante.data.model.toDataBase
import com.hallen.bustamante.data.model.toDomain
import com.hallen.bustamante.network.ProveedorService
import java.util.Date
import javax.inject.Inject

class ProveedorUseCase @Inject constructor(
    private val proveedorDao: ProveedorDao,
    private val dateDao: DateDao
) {
    suspend fun getAllProveedorFromApi(): List<Proveedor> = ProveedorService().getAllProveedores()

    suspend fun insertProveedores(proveedores: List<Proveedor>) {
        proveedorDao.insertProveedores(proveedores.map { it.toDataBase() })
    }

    suspend fun getAllProveedores(): List<Proveedor> =
        proveedorDao.getAllProveedores().map { it.toDomain() }


    suspend fun insertUpdateTime(date: Date) {
        val entity = DateEntity("proveedores", date)
        dateDao.insert(entity)
    }
}