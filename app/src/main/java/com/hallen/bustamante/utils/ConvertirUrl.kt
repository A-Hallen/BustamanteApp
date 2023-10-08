package com.hallen.bustamante.utils

import java.net.URLDecoder
import java.net.URLEncoder

class ConvertirUrl {

    private fun getName(url: String): String {
        val urlParts = url.split("/")
        val fileNameWithParams = urlParts[urlParts.size - 1]
        val fileName = fileNameWithParams.split("?")[0]
        val decodedFileName = URLDecoder.decode(fileName, "UTF-8")
        return decodedFileName.split("/").last()
    }


    operator fun invoke(url: String, nombre: String = getName(url)): String {
        try {
            // Verificar si la URL comienza con "https://storage.googleapis.com/" o "https://firebasestorage.googleapis.com/"
            if (url.startsWith("https://storage.googleapis.com/") ||
                url.startsWith("https://firebasestorage.googleapis.com/")
            ) {
                // Reemplazar el segmento "ciertocitioweb.com" con "https://bustamante.onrender.com/"
                val nuevaURL = url.replace("https://", "https://bustamante.onrender.com/")
                if (nombre.isNotEmpty()) {
                    return "$nuevaURL?filename=${URLEncoder.encode(nombre, "UTF-8")}"
                }
                return nuevaURL
            } else {
                // La URL no cumple con el formato esperado
                throw IllegalArgumentException("URL no válida")
            }
        } catch (error: Throwable) {
            println("Error al convertir la URL: ${error.message}, url: $url")
            // Devolver la URL original en caso de error
            return url
        }
    }
}