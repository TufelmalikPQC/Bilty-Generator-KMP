package com.bilty.generator.bridge

actual fun encodeToBase64(bytes: ByteArray): String {
    return platform.Foundation.NSData.create(
        bytes = bytes.refTo(0),
        length = bytes.size.toULong()
    ).base64EncodedStringWithOptions(0u)
}