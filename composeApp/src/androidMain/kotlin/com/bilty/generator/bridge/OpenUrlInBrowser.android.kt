package com.bilty.generator.bridge


import android.content.Intent
import androidx.core.content.FileProvider
import com.bilty.generator.utiles.androidContext
import java.io.File

actual fun openUrlInBrowser(url: String) {
    val tempFile = File(androidContext.cacheDir, "receipt.html")
    tempFile.writeText(url)

    val uri = FileProvider.getUriForFile(
        androidContext,
        "${androidContext.packageName}.provider",
        tempFile
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "text/html")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    androidContext.startActivity(intent)
}