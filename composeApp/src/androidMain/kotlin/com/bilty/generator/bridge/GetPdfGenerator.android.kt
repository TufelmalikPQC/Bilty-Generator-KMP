
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
        isPreviewWithImageBitmap: Boolean,
        zoomLevel: Double
    ): ByteArray? = withContext(Dispatchers.Main) {
        try {
            val html = generateRoadLineDeliveryReceipt(receipt, isPreviewWithImageBitmap,zoomLevel)
            val result = CompletableDeferred<ByteArray?>()
            val context = androidContextActivity ?: return@withContext null

            // Ensure cache directory exists
            if (!context.cacheDir.exists()) {
                context.cacheDir.mkdirs()
            }

            // Create unique filename to avoid conflicts
            val timestamp = System.currentTimeMillis()
            val file = File(context.cacheDir, "temp_receipt_$timestamp.pdf")

            println("📄 Generating PDF at: ${file.absolutePath}")

            HtmlToPdfConvertor(context).apply {
                setJavaScriptEnabled(true)
                convert(
                    pdfLocation = file,
                    htmlString = html,
                    onPdfGenerationFailed = { error ->
                        println("❌ PDF generation failed: $error")
                        // Clean up file if it exists
                        if (file.exists()) {
                            file.delete()
                        }
                        result.complete(null)
                    },
                    onPdfGenerated = { pdfFile ->
                        try {
                            println("✅ PDF generated successfully: ${pdfFile.absolutePath}")
                            println("📊 File exists: ${pdfFile.exists()}, Size: ${pdfFile.length()} bytes")

                            if (!pdfFile.exists() || pdfFile.length() == 0L) {
                                println("❌ PDF file is empty or doesn't exist")
                                result.complete(null)
                                return@convert
                            }

                            // Read the file's bytes
                            val pdfData = pdfFile.readBytes()
                            println("📦 Read ${pdfData.size} bytes from PDF")

                            // Clean up the temporary file
                            val deleted = pdfFile.delete()
                            println("🗑️ Temp file deleted: $deleted")

                            // Complete with the ByteArray data
                            result.complete(pdfData)
                        } catch (e: Exception) {
                            println("❌ Error reading PDF file: ${e.message}")
                            e.printStackTrace()

                            // Clean up file if it exists
                            if (pdfFile.exists()) {
                                pdfFile.delete()
                            }
                            result.complete(null)
                        }
                    }
                )
            }

            return@withContext result.await()
        } catch (e: Exception) {
            println("❌ PDF generation error: ${e.message}")
            e.printStackTrace()
            return@withContext null
        }
    }
}