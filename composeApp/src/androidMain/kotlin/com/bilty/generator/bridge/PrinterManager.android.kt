package com.bilty.generator.bridge

import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import biltygenerator.composeapp.generated.resources.Res
import com.bilty.generator.model.constants.Constants.BILTY_TEXT_FILE_PATH
import com.bilty.generator.model.data.PrinterInfo
import com.bilty.generator.model.enums.FontStyles
import com.bilty.generator.model.enums.PrintStatus
import com.bilty.generator.utiles.androidContext
import com.bilty.generator.utiles.androidContextActivity
import io.github.mddanishansari.html_to_pdf.HtmlToPdfConvertor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PrinterManager {
    actual fun getAvailablePrinters(): List<PrinterInfo> {
        // Android does not provide an API to list printers before opening the print dialog.
        return emptyList()
    }

    actual suspend fun printPdf(
        pdfContentData: ByteArray,
        printerName: String?,
        fontSize: Int?,
        isLandscapeMode: Boolean?,
        fontFamilyName: FontStyles?
    ): PrintStatus {
        val printManager =
            androidContextActivity?.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                ?: return PrintStatus.FAILED

        val jobName = "Receipt_Document"
        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE) // or your target size
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build()

        printManager.print(jobName, object : PrintDocumentAdapter() {
            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                try {
                    ByteArrayInputStream(pdfContentData).use { input ->
                        FileOutputStream(destination?.fileDescriptor).use { output ->
                            input.copyTo(output)
                        }
                    }
                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                }
            }

            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback.onLayoutCancelled()
                    return
                }
                val info = PrintDocumentInfo.Builder("document.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build()
                callback.onLayoutFinished(info, true)
            }
        }, printAttributes)

        // The job is handed off to the system. We can only assume it's pending.
        return PrintStatus.PENDING
    }

    actual suspend fun printBiltyTextFile(
        printerName: String?,
        fontSize: Int?,
        isLandscapeMode: Boolean?,
        fontFamilyName: FontStyles?
    ): PrintStatus = withContext(Dispatchers.IO) {
        return@withContext try {
            logToFile("\n📄 Reading test file from resources: $BILTY_TEXT_FILE_PATH")

            val testFileBytes = Res.readBytes(BILTY_TEXT_FILE_PATH)

            if (testFileBytes.isEmpty()) {
                logToFile("❌ Test file not found or empty in resources")
                return@withContext PrintStatus.FAILED
            }

            val fileContent = testFileBytes.decodeToString()
            logToFile("✅ Successfully loaded test file (${testFileBytes.size} bytes)")

            // Create simple HTML with monospace font to preserve text formatting
            val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    @page {
                        size: 5.82in 4.12in;
                        margin: 0.14in;
                    }
                    body {
                        font-family: 'Courier New', monospace;
                        font-size: ${fontSize ?: 12}pt;
                        line-height: 1.2;
                        margin: 0;
                        padding: 0;
                        white-space: pre-wrap;
                    }
                </style>
            </head>
            <body>$fileContent</body>
            </html>
        """.trimIndent()

            // Convert HTML to PDF
            val pdfFile = File(androidContext.cacheDir, "bilty_formatted.pdf")

            HtmlToPdfConvertor(androidContext).convert(
                pdfLocation = pdfFile,
                htmlString = htmlContent,
                onPdfGenerated = {
                    logToFile("📄 PDF generated successfully")
                }
            )

            logToFile("📄 PDF saved to: ${pdfFile.absolutePath}")

            // Read the generated PDF
            val pdfBytes = pdfFile.readBytes()

            // Print using Android Print Framework
            printPdf(
                pdfContentData = pdfBytes,
                printerName = printerName,
                fontSize = fontSize,
                isLandscapeMode = true,
                fontFamilyName = fontFamilyName
            )

        } catch (e: Throwable) {
            logToFile("❌ Error in printBiltyTextFile: ${e.javaClass.simpleName}: ${e.message}")
            e.printStackTrace()
            PrintStatus.FAILED
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun logToFile(message: String) {
        try {
            val logFile = File(androidContext.cacheDir, "bilty_debug_log.txt")
            val timestamp = Clock.System.now().toString()
            logFile.appendText("[$timestamp] $message\n")
            println(message)
            android.util.Log.d("PrinterManager", message)
        } catch (e: Exception) {
            println("Failed to write to log file: ${e.message}")
            android.util.Log.e("PrinterManager", "Failed to write to log file", e)
        }
    }

}