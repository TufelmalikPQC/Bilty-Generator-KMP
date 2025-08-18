package com.bilty.generator.bridge


actual fun getImageAsBase64(imageName: String): String {
    // For WASM/JS, we need to handle this asynchronously
    // This is a simplified synchronous version - in practice you'd want to use suspending functions
    return try {
        // This is a placeholder - actual implementation would need to be async
        println("Warning: Synchronous image loading not supported in WASM/JS. Use getImageAsBase64Async() instead.")
        ""
    } catch (e: Exception) {
        println("Error loading image $imageName on WASM/JS: ${e.message}")
        ""
    }
}