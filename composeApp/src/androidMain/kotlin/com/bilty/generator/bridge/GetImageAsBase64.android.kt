package com.bilty.generator.bridge


import android.util.Base64
import com.bilty.generator.utiles.androidContext
import java.io.IOException


actual fun getImageAsBase64(imageName: String): String {
    return try {

        // Try to load from assets first
        val assetManager = androidContext.assets
        val inputStream = try {
            assetManager.open("images/$imageName")
        } catch (e: IOException) {
            e.printStackTrace()
            // If not found in assets, try from raw resources
            val resourceId = androidContext.resources.getIdentifier(
                imageName.substringBeforeLast('.'),
                "raw",
                androidContext.packageName
            )
            if (resourceId != 0) {
                androidContext.resources.openRawResource(resourceId)
            } else {
                println("Warning: Image $imageName not found in Android assets or raw resources")
                return ""
            }
        }

        inputStream.use { stream ->
            val bytes = stream.readBytes()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        }
    } catch (e: Exception) {
        println("Error loading image $imageName on Android: ${e.message}")
        e.printStackTrace()
        ""
    }
}
