package com.hallen.bustamante.utils

import java.io.UnsupportedEncodingException
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

    /**
     * Invoke
     *
     * @param url La url original que se desea transformar.
     * @param nombre El nombre opcional a agregar como parametro.
     * @return la url transformada con el dominio reemplazado.
     */
    operator fun invoke(url: String, nombre: String = getName(url)): String {
        try {
            return getUrl(url, nombre)
        } catch (error: IllegalArgumentException) {
            println("Error al convertir la URL: ${error.message}, url: $url")
        } catch (error: UnsupportedEncodingException) {
            println("Error al convertir la URL: ${error.message}, url: $url")
        }
        return url
    }

    /**
     * Gets the transformed URL with the domain replaced.
     *
     * @param url The original URL to transform.
     * @param nombre The optional name to add as a parameter.
     * @return The transformed URL with the domain replaced.
     * @throws IllegalArgumentException if the URL does not match the expected format.
     */
    private fun getUrl(url: String, nombre: String): String {
        if (url.startsWith("https://storage.googleapis.com/") ||
            url.startsWith("https://firebasestorage.googleapis.com/")
        ) {
            val nuevaURL = url.replace("https://", "https://bustamante.onrender.com/")
            return if (nombre.isEmpty()) nuevaURL
            else "$nuevaURL?filename=${URLEncoder.encode(nombre, "UTF-8")}"
        }
        throw IllegalArgumentException("Invalid URL")
    }
}
