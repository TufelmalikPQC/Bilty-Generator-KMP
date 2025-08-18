package com.bilty.generator.bridge


import android.annotation.SuppressLint
import android.os.Environment
import com.bilty.generator.uiToolKit.generateRoadLineDeliveryReceipt
import com.bilty.generator.model.data.RoadLineDeliveryReceipt
import com.bilty.generator.utiles.androidContextActivity
import com.bilty.generator.model.interfaces.PdfGenerator
import io.github.mddanishansari.html_to_pdf.HtmlToPdfConvertor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

actual fun getPdfGenerator(): PdfGenerator = PdfGeneratorAndroid()

class PdfGeneratorAndroid : PdfGenerator {

    @SuppressLint("SetJavaScriptEnabled")
    override suspend fun generatePdf(receipt: RoadLineDeliveryReceipt, isPreviewWithImageBitmap:  Boolean): Boolean =
        withContext(Dispatchers.Main) {

            val html = generateRoadLineDeliveryReceipt(receipt,isPreviewWithImageBitmap)
            val result = CompletableDeferred<Boolean>()

            val context = androidContextActivity
            if (context == null) {
                println("❌ No Activity context available.")
                return@withContext false
            }

            val state = Environment.getExternalStorageState()
            if (state != Environment.MEDIA_MOUNTED) {
                println("❌ External storage not available")
                return@withContext false
            }

            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) downloadsDir.mkdirs()
            val file = File(downloadsDir, "receipt-${receipt.receiptNumber}.pdf")

            val htmlToPdfConvertor = HtmlToPdfConvertor(context)

            // Optional: Enable JS if your HTML needs it
            htmlToPdfConvertor.setJavaScriptEnabled(true)

            htmlToPdfConvertor.convert(
                pdfLocation = file,
                htmlString = html,
                onPdfGenerationFailed = { exception ->
                    println("❌ Error during PDF generation: ${exception.message}")
                    result.complete(false)
                },
                onPdfGenerated = { pdfFile ->
                    println("✅ PDF saved successfully at: ${pdfFile.absolutePath}")
                    result.complete(true)
                }
            )

            result.await()
        }
}