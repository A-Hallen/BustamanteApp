package com.hallen.bustamante.service

import android.content.Context
import com.hallen.bustamante.utils.ConvertirUrl
import com.orhanobut.logger.Logger
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.security.MessageDigest
import java.util.Locale

private const val HASH_BUFFER_SIZE = 8192
private const val DOWNLOAD_BUFFER_SIZE = 1024

abstract class Updater(private val context: Context) {
    open var intentosMaximos = 0

    open fun obtenerNombreArchivo(url: String, directorio: String): String {
        Logger.i("url: $url, directorio: $directorio")
        val separador = url.lastIndexOf("/")
        val separador2 = url.indexOf("?")

        return if (separador != -1) {
            url.substring(separador + 1, separador2)
        } else "archivo"
    }

    open fun storeFileAndGetPath(
        serverUrl: String,
        directorio: String,
        name: String = "",
        hash: String = ""
    ): String? {
        if (intentosMaximos > 1) return null
        intentosMaximos++
        return try {
            val url = ConvertirUrl().invoke(serverUrl)

            downloadFileIfNotExists(directorio, name, hash, url)
        } catch (e: IOException) {
            Logger.e(e.cause, e.message.toString())
            null
        }
    }

    private fun getFileHash(archivo: File): String {
        val md5Digest = MessageDigest.getInstance("MD5")
        val inputStream = FileInputStream(archivo)
        val buffer = ByteArray(HASH_BUFFER_SIZE)
        var bytesRead = inputStream.read(buffer)

        while (bytesRead != -1) {
            md5Digest.update(buffer, 0, bytesRead)
            bytesRead = inputStream.read(buffer)
        }

        inputStream.close()

        val bytes = md5Digest.digest()
        val sb = StringBuilder()

        for (i in bytes.indices) {
            sb.append(String.format(Locale.US, "%02x", bytes[i]))
        }

        return sb.toString()
    }

    private fun downloadFileIfNotExists(
        directorio: String, name: String, hash: String, url: String
    ): String? {
        val nombreArchivo = name.ifEmpty {
            obtenerNombreArchivo(url, directorio)
        }

        val directorioDestino = File(context.filesDir, directorio)
        if (!directorioDestino.exists()) directorioDestino.mkdirs()
        val archivoDestino = File(directorioDestino, nombreArchivo)
        if (archivoDestino.exists()) return nombreArchivo

        val inputStream = BufferedInputStream(URL(url).openStream())
        val outputStream = FileOutputStream(archivoDestino)
        val data = ByteArray(DOWNLOAD_BUFFER_SIZE)
        var count: Int
        while (inputStream.read(data).also { count = it } != -1) {
            outputStream.write(data, 0, count)
        }

        outputStream.flush()
        outputStream.close()
        inputStream.close()

        return if (hash.isNotBlank()) {
            val localHash = getFileHash(archivoDestino)
            if (localHash != hash) {
                Logger.i("localHash: $localHash, hash: $hash")
                archivoDestino.delete()
                storeFileAndGetPath(url, directorio, name, hash)
            } else nombreArchivo
        } else nombreArchivo
    }


}
