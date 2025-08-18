package com.bilty.generator.bridge


import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.dataWithContentsOfFile
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePNGRepresentation

actual fun getImageAsBase64(imageName: String): String {
    return try {
        // Get the main bundle
        val bundle = NSBundle.mainBundle

        // Extract name and extension
        val nameWithoutExt = imageName.substringBeforeLast('.')
        val extension = imageName.substringAfterLast('.', "png")

        // Find the file path in bundle
        val path = bundle.pathForResource(nameWithoutExt, extension)

        if (path == null) {
            println("Warning: Image $imageName not found in iOS bundle")
            return ""
        }

        // Method 1: Load as UIImage and convert to data
        val image = UIImage.imageWithContentsOfFile(path)
        if (image != null) {
            val imageData = when (extension.lowercase()) {
                "jpg", "jpeg" -> UIImageJPEGRepresentation(image, 1.0)
                else -> UIImagePNGRepresentation(image)
            }

            if (imageData != null) {
                val base64 = imageData.base64EncodedStringWithOptions(0u)
                return base64
            }
        }

        // Method 2: Load as raw data if UIImage fails
        val data = NSData.dataWithContentsOfFile(path)
        if (data != null) {
            val base64 = data.base64EncodedStringWithOptions(0u)
            return base64
        }

        println("Warning: Failed to load image data for $imageName")
        ""
    } catch (e: Exception) {
        println("Error loading image $imageName on iOS: ${e.message}")
        e.printStackTrace()
        ""
    }
}