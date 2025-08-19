package com.bilty.generator.bridge

import com.bilty.generator.model.data.PrinterInfo
import com.bilty.generator.model.enums.PrintStatus
import kotlinx.browser.window
import org.khronos.webgl.Int8Array
import org.khronos.webgl.set
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PrinterManager {
    actual fun getAvailablePrinters(): List<PrinterInfo> {
        return emptyList()
    }

    actual suspend fun printPdf(data: ByteArray, printerName: String?): PrintStatus {
        return try {
            val int8Array = Int8Array(data.size)
            data.forEachIndexed { index, byte ->
                // CORRECTED: Use standard indexed assignment
                int8Array[index] = byte
            }

            val arrayContainer = listOf<JsAny?>(int8Array).toJsArray()
            val blob = Blob(arrayContainer, BlobPropertyBag(type = "application/pdf"))

            val url = URL.createObjectURL(blob)
            window.open(url, "_blank")
            URL.revokeObjectURL(url)
            PrintStatus.PENDING
        } catch (e: Exception) {
            println("Wasm printing failed: ${e.message}")
            PrintStatus.FAILED
        }
    }
}