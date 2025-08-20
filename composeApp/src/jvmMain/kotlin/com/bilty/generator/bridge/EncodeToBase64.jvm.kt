package com.bilty.generator.bridge

actual fun encodeToBase64(bytes: ByteArray): String {
    return java.util.Base64.getEncoder().encodeToString(bytes)
}