package com.bilty.generator.bridge

import com.bilty.generator.model.constants.PDF
import com.bilty.generator.model.data.RoadLineDeliveryReceipt
import com.bilty.generator.model.interfaces.PdfGenerator
import com.bilty.generator.uiToolKit.generateRoadLineDeliveryReceipt
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

actual fun getPdfGenerator(): PdfGenerator = PdfGeneratorDesktop()

class PdfGeneratorDesktop : PdfGenerator {
    override suspend fun generatePdf(
        receipt: RoadLineDeliveryReceipt,
        isPreviewWithImageBitmap: Boolean,
        zoomLevel: Double
    ): ByteArray? = withContext(Dispatchers.IO) {
        return@withContext try {
            val html = generateRoadLineDeliveryReceipt(receipt, isPreviewWithImageBitmap,zoomLevel)

            // 1. Generate the PDF into memory first
            val outputStream = java.io.ByteArrayOutputStream()
            PdfRendererBuilder().run {
                withHtmlContent(html, null)
                toStream(outputStream)
                run()
            }
            val pdfData = outputStream.toByteArray()

            // 2. Now, save the generated data to a file
            val outputPath =
                System.getProperty("user.home") + PDF.pdfSavePath(receipt.receiptNumber)
            val file = File(outputPath)
            // Ensure parent directories exist
            file.parentFile?.mkdirs()

            FileOutputStream(file).use { fileStream ->
                fileStream.write(pdfData)
            }
            println("✅ PDF saved to: ${file.absolutePath}")

            // 3. Finally, return the data for printing
            pdfData
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}