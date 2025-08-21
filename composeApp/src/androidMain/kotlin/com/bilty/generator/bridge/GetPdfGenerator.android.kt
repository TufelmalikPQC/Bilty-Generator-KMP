package com.bilty.generator.bridge

import android.annotation.SuppressLint
import android.os.Environment
import com.bilty.generator.model.constants.PDF
import com.bilty.generator.model.data.RoadLineDeliveryReceipt
import com.bilty.generator.model.interfaces.PdfGenerator
import com.bilty.generator.uiToolKit.generateRoadLineDeliveryReceipt
import com.bilty.generator.utiles.androidContextActivity
import io.github.mddanishansari.html_to_pdf.HtmlToPdfConvertor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

actual fun getPdfGenerator(): PdfGenerator = PdfGeneratorAndroid()

class PdfGeneratorAndroid : PdfGenerator {
    @SuppressLint("SetJavaScriptEnabled")
    override suspend fun generatePdf(
        receipt: RoadLineDeliveryReceipt,
        isPreviewWithImageBitmap: Boolean,
        isWantToSavePDFLocally: Boolean,
        zoomLevel: Double
    ): ByteArray? = withContext(Dispatchers.Main) {
        try {
            val html = generateRoadLineDeliveryReceipt(
                receipt,
                isPreviewWithImageBitmap,
                false,
                zoomLevel
            )
            val result = CompletableDeferred<ByteArray?>()
            val context = androidContextActivity ?: return@withContext null

            // Ensure cache directory exists for the temporary file
            if (!context.cacheDir.exists()) {
                context.cacheDir.mkdirs()
            }

            // Create a unique temporary filename to avoid conflicts
            val timestamp = System.currentTimeMillis()
            val tempFile = File(context.cacheDir, "temp_receipt_$timestamp.pdf")

            println("📄 Generating temporary PDF at: ${tempFile.absolutePath}")

            HtmlToPdfConvertor(context).apply {
                setJavaScriptEnabled(true)
                convert(
                    pdfLocation = tempFile,
                    htmlString = html,
                    onPdfGenerationFailed = { error ->
                        println("❌ PDF generation failed: $error")
                        if (tempFile.exists()) {
                            tempFile.delete()
                        }
                        result.complete(null)
                    },
                    onPdfGenerated = { pdfFile ->
                        try {
                            println("✅ PDF generated successfully: ${pdfFile.absolutePath}")
                            if (!pdfFile.exists() || pdfFile.length() == 0L) {
                                println("❌ PDF file is empty or doesn't exist")
                                result.complete(null)
                                return@convert
                            }

                            // 1. Read the file's bytes into memory
                            val pdfData = pdfFile.readBytes()
                            println("📦 Read ${pdfData.size} bytes from PDF")

                            // 2. If requested, save the data to a permanent public file
                            if (isWantToSavePDFLocally) {
                                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                val outputPath = File(downloadsDir, PDF.pdfSavePath(receipt.receiptNumber))

                                // Ensure parent directories exist
                                outputPath.parentFile?.mkdirs()

                                FileOutputStream(outputPath).use { fileStream ->
                                    fileStream.write(pdfData)
                                }
                                println("✅ PDF saved to: ${outputPath.absolutePath}")
                            }

                            // 3. Clean up the temporary file
                            val deleted = pdfFile.delete()
                            println("🗑️ Temp file deleted: $deleted")

                            // 4. Complete with the ByteArray data for in-app use
                            result.complete(pdfData)
                        } catch (e: Exception) {
                            println("❌ Error processing generated PDF: ${e.message}")
                            e.printStackTrace()
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
            println("❌ Top-level PDF generation error: ${e.message}")
            e.printStackTrace()
            return@withContext null
        }
    }
}
