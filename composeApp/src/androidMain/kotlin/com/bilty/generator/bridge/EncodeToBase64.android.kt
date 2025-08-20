package com.bilty.generator.bridge

actual fun encodeToBase64(bytes: ByteArray): String {
    return android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
}