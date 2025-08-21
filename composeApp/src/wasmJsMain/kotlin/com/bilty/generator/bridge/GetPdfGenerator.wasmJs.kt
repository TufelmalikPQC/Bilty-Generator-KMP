package com.bilty.generator.bridge

import com.bilty.generator.model.data.RoadLineDeliveryReceipt
import com.bilty.generator.model.interfaces.PdfGenerator
import com.bilty.generator.uiToolKit.generateRoadLineDeliveryReceipt
import kotlinx.browser.document
import kotlinx.coroutines.await
import org.khronos.webgl.Int8Array
import org.w3c.files.Blob

// STEP 1: Update the interface to include the 'outputPdf' function,
// which returns the data instead of saving it.
external interface Html2Pdf {
    fun from(element: org.w3c.dom.Element): Html2Pdf
    fun save(fileName: String)
    fun outputPdf(type: String = definedExternally): js.core.Promise<Blob>
}

external fun html2pdf(): Html2Pdf

actual fun getPdfGenerator(): PdfGenerator = PdfGeneratorWeb()

class PdfGeneratorWeb : PdfGenerator {
    // STEP 2: The function must now return a nullable ByteArray?
    override suspend fun generatePdf(
        receipt: RoadLineDeliveryReceipt,
        isPreviewWithImageBitmap: Boolean,
        isWantToSavePDFLocally: Boolean,
        zoomLevel: Double
    ): ByteArray? {
        return try {
            val html = generateRoadLineDeliveryReceipt(
                receipt,
                isPreviewWithImageBitmap,
                false,
                zoomLevel
            )
            val div = document.createElement("div") as org.w3c.dom.HTMLDivElement
            div.innerHTML = html
            document.body?.appendChild(div)

            // STEP 3: Instead of .save(), call .outputPdf("blob") and wait for the result.
            // The .await() function pauses until the PDF data is ready.
            val blob = html2pdf().from(div).outputPdf("blob").await()
            div.remove()

            // STEP 4: Convert the Blob data into an ArrayBuffer.
            val arrayBuffer = blob.arrayBuffer().await()

            // STEP 5: Convert the ArrayBuffer into a Kotlin ByteArray and return it.
            Int8Array(arrayBuffer).unsafeCast<ByteArray>()
        } catch (e: Exception) {
            console.error("Wasm PDF generation for printing failed:", e)
            null
        }
    }
}