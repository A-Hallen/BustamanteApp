package com.hallen.bustamante.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import com.hallen.bustamante.data.model.Information
import com.hallen.bustamante.data.model.Proveedor
import com.hallen.bustamante.domain.ProductUseCase
import com.hallen.bustamante.domain.ProveedorUseCase
import com.hallen.bustamante.utils.ConvertirUrl
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.security.MessageDigest
import java.util.Date
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
        startJob()
        return true
    }

    private fun startJob() {
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

        private var intentosMaximos = 0
        private fun storeFileAndGetPath(
            serverUrl: String,
            directorio: String,
            name: String = "",
            hash: String = ""
        ): String? {
            try {
                val url = ConvertirUrl().invoke(serverUrl)
                if (intentosMaximos > 1) return null
                intentosMaximos++
                val nombreArchivo = name.ifEmpty {
                    obtenerNombreArchivo(url, directorio)
                }
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

                if (hash.isNotBlank()) {
                    val localHash = getFileHash(archivoDestino)
                    if (localHash == hash) return nombreArchivo
                    Logger.i("localHash: $localHash, hash: $hash")
                    archivoDestino.delete()
                    return storeFileAndGetPath(url, directorio, name, hash)
                }
                return nombreArchivo
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        private fun getFileHash(archivo: File): String {
            val md5Digest = MessageDigest.getInstance("MD5")
            val inputStream = FileInputStream(archivo)
            val buffer = ByteArray(8192)
            var bytesRead = inputStream.read(buffer)

            while (bytesRead != -1) {
                md5Digest.update(buffer, 0, bytesRead)
                bytesRead = inputStream.read(buffer)
            }

            inputStream.close()

            val bytes = md5Digest.digest()
            val sb = StringBuilder()

            for (i in bytes.indices) {
                sb.append(String.format("%02x", bytes[i]))
            }

            return sb.toString()
        }

        private fun obtenerNombreArchivo(url: String, directorio: String): String {
            Logger.i("url: $url, directorio: $directorio")
            val separador = url.lastIndexOf("/")
            val separador2 = url.indexOf("?")
            return if (separador != -1) {
                url.substring(separador + 1, separador2)
            } else {
                "archivo"
            }
        }

        private suspend fun updateProductos(): Boolean {
            val hasToUpdate = productUseCase.hasToUpdate("productos")
            Logger.i("Hay que actualizar los productos: $hasToUpdate")
            if (hasToUpdate != true) return false
            val productos = productUseCase.getAllProductsFromApi()
            productos.map {
                intentosMaximos = 0
                it.imagen = storeFileAndGetPath(it.imagen, "imagenes") ?: return false
            }
            if (productos.isEmpty()) return false
            productUseCase.insertProducts(productos)
            return true
        }

        private suspend fun updateProveedores(): Boolean {
            Logger.i("My job scheduler")
            val hasToUpdate = productUseCase.hasToUpdate("proveedores")
            Logger.i("Hay que actualizar los proveedores: $hasToUpdate")
            if (hasToUpdate != true) return false
            val proveedores = proveedorUseCase.getAllProveedorFromApi()
            val newProveedores: List<Proveedor> = proveedores.map { proveedor ->
                proveedor.productos?.map {
                    intentosMaximos = 0
                    it.imagen = storeFileAndGetPath(it.imagen, "imagenes") ?: return false
                }

                val newInformation: List<Information>? = proveedor.informacion?.map {
                    intentosMaximos = 0
                    val url =
                        storeFileAndGetPath(
                            it.url,
                            "documentos",
                            "${it.nombre}.${it.extension}",
                            it.hash
                        ) ?: return false
                    Information(
                        nombre = it.nombre,
                        hash = it.hash,
                        url = url,
                        extension = it.extension
                    )
                }
                proveedor.copy(informacion = newInformation)
            }
            proveedorUseCase.insertProveedores(newProveedores)

            return true
        }

        private suspend fun run() {
            val intent = Intent("jobservice.to.activity.update")
            if (updateProductos()) {
                intent.putExtra("clave", "productos")
                sendBroadcast(intent)
                productUseCase.insertUpdateTime(Date())
            }
            if (updateProveedores()) {
                intent.putExtra("clave", "proveedores")
                sendBroadcast(intent)
                proveedorUseCase.insertUpdateTime(Date())
            }
            endJob()
        }
    }
}
