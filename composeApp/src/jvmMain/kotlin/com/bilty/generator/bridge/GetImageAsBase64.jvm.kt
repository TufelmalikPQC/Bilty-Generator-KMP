package com.bilty.generator.bridge


import java.util.Base64

actual fun getImageAsBase64(imageName: String): String {
    return try {
        // The correct path for desktop resources is rooted in the resources folder
        val resourcePath = "/$imageName"
        val imageStream = ImageUtils::class.java.getResourceAsStream(resourcePath)

        if (imageStream != null) {
            imageStream.use { stream ->
                val bytes = stream.readBytes()
                Base64.getEncoder().encodeToString(bytes)
            }
        } else {
            println("Warning: Image $imageName not found in desktop resources at path: $resourcePath")
            ""
        }
    } catch (e: Exception) {
        println("Error loading image $imageName on desktop: ${e.message}")
        e.printStackTrace()
        ""
    }
}

// Helper object for desktop (optional)
object ImageUtils {
    // This can be used for additional desktop-specific functionality
}
