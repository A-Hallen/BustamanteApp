package com.example.bustamante.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import com.example.bustamante.data.model.Proveedor
import com.example.bustamante.domain.ProductUseCase
import com.example.bustamante.domain.ProveedorUseCase
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import javax.inject.Inject

@AndroidEntryPoint
class AndroidScheduler : JobService() {

    @Inject
    lateinit var productUseCase: ProductUseCase

    @Inject
    lateinit var proveedorUseCase: ProveedorUseCase

    private var myAsyncTask: MyAsyncTask? = null
    private var jobParameters: JobParameters? = null

    override fun onStartJob(p0: JobParameters?): Boolean {
        jobParameters = p0
        startJob(p0)
        return true
    }

    private fun startJob(params: JobParameters?) {
        myAsyncTask = MyAsyncTask()
        myAsyncTask?.start()
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        if (myAsyncTask != null) {
            if (myAsyncTask?.isAlive == true) {
                myAsyncTask?.interrupt()
            }
        }
        return true
    }

    inner class MyAsyncTask {
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
            jobFinished(jobParameters, true)
        }

        private fun storeFileAndGetPath(
            url: String,
            directorio: String
        ): String? {
            try {
                val nombreArchivo = obtenerNombreArchivo(url, directorio)
                val directorioDestino = File(applicationContext.filesDir, directorio)
                if (!directorioDestino.exists()) directorioDestino.mkdirs()

                val archivoDestino = File(directorioDestino, nombreArchivo)
                if (archivoDestino.exists()) return nombreArchivo

                val inputStream = BufferedInputStream(URL(url).openStream())
                val outputStream = FileOutputStream(archivoDestino)
                val data = ByteArray(1024)
                var count: Int
                while (inputStream.read(data).also { count = it } != -1) {
                    outputStream.write(data, 0, count)
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()

                return nombreArchivo
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        private fun obtenerNombreArchivo(url: String, directorio: String): String {
            Logger.i("url: $url, directorio: $directorio")
            return when (url.contains("documentos%2F1%2F")) {
                false -> {
                    val separador = url.lastIndexOf("/")
                    val separador2 = url.indexOf("?")
                    if (separador != -1) {
                        url.substring(separador + 1, separador2)
                    } else {
                        "archivo"
                    }
                }

                true -> {
                    val inicio = url.lastIndexOf("documentos%2F1%2F") + 17
                    val fin = url.indexOf("?")
                    url.substring(inicio, fin)
                }
            }


        }

        private suspend fun updateProductos() {
            val hasToUpdate = productUseCase.hasToUpdate("productos")
            Logger.i("Hay que actualizar los productos: $hasToUpdate")
            if (hasToUpdate != true) return
            val productos = productUseCase.getAllProductsFromApi()
            productos.map {
                it.imagen = storeFileAndGetPath(it.imagen, "imagenes") ?: return
            }
            if (productos.isEmpty()) return
            productUseCase.insertProducts(productos)
            val intent = Intent("jobservice.to.activity.update")
            intent.putExtra("clave", "productos")
            sendBroadcast(intent)
        }

        private suspend fun updateProveedores() {
            Logger.i("My job scheduler")
            val hasToUpdate = productUseCase.hasToUpdate("proveedores")
            Logger.i("Hay que actualizar los proveedores: $hasToUpdate")
            if (hasToUpdate != true) return
            val proveedores = proveedorUseCase.getAllProveedorFromApi()
            val newProveedores: List<Proveedor> = proveedores.map { proveedor ->
                proveedor.productos?.map {
                    it.imagen = storeFileAndGetPath(it.imagen, "imagenes") ?: ""
                }

                val newInformation: Map<String, String>? = proveedor.informacion?.map {
                    val path = storeFileAndGetPath(it.value, "documentos") ?: ""
                    Pair(it.key, path)
                }?.toMap()

                proveedor.copy(informacion = newInformation)
            }
            proveedorUseCase.insertProveedores(newProveedores)
            val intent = Intent("jobservice.to.activity.update")
            intent.putExtra("clave", "proveedores")
            sendBroadcast(intent)
        }

        private suspend fun run() {
            updateProductos()
            updateProveedores()
            endJob()
        }
    }
}