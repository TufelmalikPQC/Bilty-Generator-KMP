package com.bilty.generator.bridge


import android.annotation.SuppressLint
import com.bilty.generator.model.data.RoadLineDeliveryReceipt
import com.bilty.generator.model.interfaces.PdfGenerator
import com.bilty.generator.uiToolKit.generateRoadLineDeliveryReceipt
import com.bilty.generator.utiles.androidContextActivity
import io.github.mddanishansari.html_to_pdf.HtmlToPdfConvertor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

actual fun getPdfGenerator(): PdfGenerator = PdfGeneratorAndroid()

class PdfGeneratorAndroid : PdfGenerator {
    @SuppressLint("SetJavaScriptEnabled")
    override suspend fun generatePdf(
        receipt: RoadLineDeliveryReceipt,
        isPreviewWithImageBitmap: Boolean
    ): ByteArray? = withContext(Dispatchers.Main) { // <-- Return ByteArray?
        val html = generateRoadLineDeliveryReceipt(receipt, isPreviewWithImageBitmap)

        // Use a CompletableDeferred that can hold a nullable ByteArray
        val result = CompletableDeferred<ByteArray?>()

        val context = androidContextActivity ?: return@withContext null

        // Save to a temporary file in the app's cache directory
        val file = File(context.cacheDir, "temp_receipt.pdf")

        HtmlToPdfConvertor(context).apply {
            setJavaScriptEnabled(true)
            convert(
                pdfLocation = file,
                htmlString = html,
                onPdfGenerationFailed = {
                    result.complete(null) // Complete with null on failure
                },
                onPdfGenerated = { pdfFile ->
                    // 1. On success, read the file's bytes
                    val pdfData = pdfFile.readBytes()
                    // 2. Clean up the temporary file
                    pdfFile.delete()
                    // 3. Complete with the ByteArray data
                    result.complete(pdfData)
                }
            )
        }
        result.await()
    }
}