package com.bilty.generator.bridge

actual fun encodeToBase64(bytes: ByteArray): String {
    return manualBase64Encode(bytes)
}

private fun manualBase64Encode(bytes: ByteArray): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    var result = ""
    var i = 0
    while (i < bytes.size) {
        val b1 = bytes[i].toInt() and 0xFF
        val b2 = if (i + 1 < bytes.size) bytes[i + 1].toInt() and 0xFF else 0
        val b3 = if (i + 2 < bytes.size) bytes[i + 2].toInt() and 0xFF else 0

        val bitmap = (b1 shl 16) or (b2 shl 8) or b3

        result += chars[(bitmap shr 18) and 63]
        result += chars[(bitmap shr 12) and 63]
        result += if (i + 1 < bytes.size) chars[(bitmap shr 6) and 63] else '='
        result += if (i + 2 < bytes.size) chars[bitmap and 63] else '='

        i += 3
    }
    return result
}
