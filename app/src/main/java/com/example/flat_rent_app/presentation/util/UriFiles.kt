package com.example.flat_rent_app.presentation.util

import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/**
 * Helpers to convert content:// Uri selected from gallery into a temp File.
 * (Needed because PhotoRepository expects a File.)
 */
object UriFiles {

    fun copyToCache(context: android.content.Context, uri: Uri): File {
        val resolver = context.contentResolver

        val ext = resolver.getType(uri)
            ?.substringAfterLast('/')
            ?.takeIf { it.isNotBlank() }
            ?: "jpg"

        val outFile = File.createTempFile("upload_", ".$ext", context.cacheDir)

        resolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "Не удалось открыть файл" }
            FileOutputStream(outFile).use { output ->
                input.copyTo(output)
            }
        }

        return outFile
    }
}
