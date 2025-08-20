package com.bilty.generator.uiToolKit

import biltygenerator.composeapp.generated.resources.Res
import com.bilty.generator.bridge.encodeToBase64

suspend fun getImageAsBase64Code(imageName: String): String {
    return try {
        println("Loading image: $imageName")
        val imageBytes = Res.readBytes("files/images/$imageName")
        println("Image loaded successfully, bytes size: ${imageBytes.size}")
        encodeToBase64(imageBytes)
    } catch (e: Exception) {
        println("Error loading image $imageName: ${e.message}")
        e.printStackTrace()
        ""
    }
}