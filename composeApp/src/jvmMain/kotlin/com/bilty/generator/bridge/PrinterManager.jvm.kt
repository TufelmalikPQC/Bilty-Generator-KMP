package com.bilty.generator.bridge

import com.bilty.generator.model.data.PrinterInfo
import com.bilty.generator.model.enums.PrintStatus
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.printing.PDFPrintable
import org.apache.pdfbox.printing.Scaling
import java.awt.print.PageFormat
import java.awt.print.PrinterJob
import javax.print.PrintService
import javax.print.PrintServiceLookup

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PrinterManager {

    actual fun getAvailablePrinters(): List<PrinterInfo> {
        val printServices = PrintServiceLookup.lookupPrintServices(null, null)
        val defaultService = PrintServiceLookup.lookupDefaultPrintService()
        return printServices.map { service ->
            PrinterInfo(name = service.name, isDefault = service == defaultService)
        }
    }

    actual suspend fun printPdf(data: ByteArray, printerName: String?): PrintStatus {
        println("🖨️ PrinterManager.printPdf called with ${data.size} bytes of data")

        // This function now assumes valid PDF data is being passed in.
        if (data.isEmpty()) {
            println("❌ Error: Received empty data for printing.")
            return PrintStatus.FAILED
        }

        val printServices = PrintServiceLookup.lookupPrintServices(null, null)
        println("📋 Available print services: ${printServices.map { it.name }}")

        val selectedService: PrintService? = if (printerName != null) {
            printServices.find { it.name == printerName }
        } else {
            PrintServiceLookup.lookupDefaultPrintService()
        }

        if (selectedService == null) {
            println("❌ Error: Printer '$printerName' not found or no default printer is available.")
            println("📋 Available printers: ${printServices.joinToString(", ") { it.name }}")
            return PrintStatus.FAILED
        }

        println("✅ Selected printer: ${selectedService.name}")

        var document: PDDocument? = null
        return try {
            println("📄 Loading PDF document from byte array...")
            document = PDDocument.load(data)
            println("✅ PDF document loaded successfully, pages: ${document.numberOfPages}")

            val job = PrinterJob.getPrinterJob()
            job.printService = selectedService
            job.setPrintable(PDFPrintable(document))

            // Create a PageFormat in landscape
            val pageFormat = job.defaultPage()
            pageFormat.orientation = PageFormat.LANDSCAPE
            println("🖨️ Showing print dialog...")
            if (job.printDialog()) {
                println("✅ User confirmed print, starting print job...")
                job.setPrintable(PDFPrintable(document, Scaling.STRETCH_TO_FIT), pageFormat)
                job.print()
                println("✅ Print job completed successfully")
                PrintStatus.SUCCESS
            } else {
                println("⚠️ User cancelled print dialog")
                PrintStatus.CANCELLED
            }
        } catch (e: Exception) {
            println("❌ Error during printing: ${e.message}")
            e.printStackTrace()
            PrintStatus.FAILED
        } finally {
            document?.close()
            println("🔄 PDF document closed")
        }
    }
}