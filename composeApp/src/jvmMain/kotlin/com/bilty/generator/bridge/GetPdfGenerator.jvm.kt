package com.bilty.generator.bridge

import com.bilty.generator.model.constants.PDF
import com.bilty.generator.model.data.RoadLineDeliveryReceipt
import com.bilty.generator.model.interfaces.PdfGenerator
import com.bilty.generator.uiToolKit.generateRoadLineDeliveryReceipt
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.PageSizeUnits
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


actual fun getPdfGenerator(): PdfGenerator = PdfGeneratorDesktop()

class PdfGeneratorDesktop : PdfGenerator {
    override suspend fun generatePdf(
        receipt: RoadLineDeliveryReceipt,
        isPreviewWithImageBitmap: Boolean
    ): Boolean =
        withContext(Dispatchers.IO) {
            val html = generateRoadLineDeliveryReceipt(receipt, isPreviewWithImageBitmap)
            val outputPath =
                System.getProperty("user.home") + PDF.pdfSavePath(receipt.receiptNumber)
            val file = File(outputPath)
            println("PDF saved to: ${file.absolutePath}")
            return@withContext try {
                val pdfRendererBuilder = PdfRendererBuilder()
                pdfRendererBuilder.run {
                    withHtmlContent(html, null)
                    toStream(FileOutputStream(file))
                    run()
                }


                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
}