package com.bilty.generator.bridge

import com.bilty.generator.model.constants.Constants.Fonts.FONT_FAMILY_NAME
import com.bilty.generator.model.constants.PDF
import com.bilty.generator.model.data.RoadLineDeliveryReceipt
import com.bilty.generator.model.interfaces.PdfGenerator
import com.bilty.generator.uiToolKit.generateRoadLineDeliveryReceipt
import com.bilty.generator.uiToolKit.getFontFamilyName
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder
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
        isWantToSavePDFLocally: Boolean,
        zoomLevel: Double,
        fontSize: Int,
        isLandscapeMode: Boolean,
        fontFamilyName: String
    ): ByteArray? = withContext(Dispatchers.IO) {
        return@withContext try {
            println("Content Data : ${receipt.receiptNumber}")
            val html = generateRoadLineDeliveryReceipt(
                receipt = receipt,
                isPreviewWithImageBitmap = isPreviewWithImageBitmap,
                isForPreview = false,
                zoomLevel = zoomLevel,
                fontSize = fontSize,
                isLandscapeMode = isLandscapeMode
            )

            println("Content Html Is Empty : ${html.isEmpty()}")
            // 1. Generate the PDF into memory first
            val outputStream = java.io.ByteArrayOutputStream()

            try {
                val fontName = getFontFamilyName(fontFamilyName)
                val url = this::class.java.classLoader.getResource(fontName)
                println("Resolved font URL: $url")

                val fontStream = this::class.java.classLoader
                    .getResourceAsStream(fontName)
                    ?: throw IllegalArgumentException("Font resource not found")

                PdfRendererBuilder().run {
                    useFont({ fontStream }, FONT_FAMILY_NAME)

                    // Set exact page size for receipt: 148mm × 105mm (5.82677" × 4.13386")
                    val (pageWidth, pageHeight) = if (isLandscapeMode) {
                        // Landscape: 148mm wide × 105mm tall = 5.82677" × 4.13386"
                        5.82677f to 4.13386f
                    } else {
                        // Portrait: 105mm wide × 148mm tall = 4.13386" × 5.82677"
                        4.13386f to 5.82677f
                    }
                    
                    println("📐 Setting PDF page size: ${pageWidth}\" × ${pageHeight}\" (${if (isLandscapeMode) "landscape" else "portrait"})")
                    
                    useDefaultPageSize(
                        pageWidth,
                        pageHeight,
                        BaseRendererBuilder.PageSizeUnits.INCHES
                    )

                    withHtmlContent(html, null)
                    toStream(outputStream)

                    run()
                }

            } catch (e: Exception) {
                println("PDF generation failed: ${e.message}")
                e.printStackTrace()
                return@withContext null
            }

            val pdfData = outputStream.toByteArray()

            println("📄 PDF generation completed: ${pdfData.size} bytes")

            // 2. If requested, save the generated data to a local file
            if (isWantToSavePDFLocally) {
                val outputPath =
                    System.getProperty("user.home") + PDF.pdfSavePath(receipt.receiptNumber)
                val file = File(outputPath)
                // Ensure parent directories exist
                file.parentFile?.mkdirs()

                FileOutputStream(file).use { fileStream ->
                    fileStream.write(pdfData)
                }
                println("✅ PDF saved to: ${file.absolutePath}")
            }

            // 3. Finally, return the data for printing
            pdfData
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}