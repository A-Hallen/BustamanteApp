package com.hallen.bustamante.service

import android.content.Context
import android.content.Intent
import com.hallen.bustamante.domain.ProductUseCase
import com.hallen.bustamante.domain.ProveedorUseCase
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class JobCoroutineTask(
    private val proveedorUseCase: ProveedorUseCase,
    private val productUseCase: ProductUseCase,
    private val context: Context,
    private val jobFinished: (Boolean) -> Unit
) {


    var isAlive = false

    fun interrupt() {
        isAlive = false
    }

    fun start() {
        isAlive = true
        CoroutineScope(Dispatchers.IO).launch {
            run()
        }
    }

    private fun endJob() {
        Logger.i("Job ended")
        isAlive = false
        jobFinished(true)
    }

    private suspend fun run() {
        Logger.i("My job scheduler")
        val intent = Intent("jobservice.to.activity.update")
        val updatedProduct = ProductUpdater(context, productUseCase).updateProductosIfNeeded()
        if (updatedProduct == true) {
            intent.putExtra("clave", "productos")
            context.sendBroadcast(intent)
            productUseCase.insertUpdateTime(Date())
        }
        val updateProveedor = ProveedorUpdater(context, proveedorUseCase)
            .updateProveedoresIfNeeded(productUseCase)
        if (updateProveedor == true) {
            intent.putExtra("clave", "proveedores")
            context.sendBroadcast(intent)
            proveedorUseCase.insertUpdateTime(Date())
        }
        endJob()
    }
}
