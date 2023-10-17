package com.hallen.bustamante.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.util.Locale

class ReadFIle {

    fun openDocument(context: Context, filePath: String) {
        val file = File("${context.filesDir}/documentos", filePath)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, getMimeType(filePath))
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            // Manejar la excepción si no se encuentra una aplicación para abrir el archivo
        }
    }

    private fun getMimeType(filePath: String): String {
        val extension = filePath.substring(filePath.lastIndexOf(".") + 1)
            .lowercase(Locale.US)

        return when (extension) {
            "doc" -> "application/msword"
            "pdf" -> "application/pdf"
            else -> "application/msword"
        }
    }
}
