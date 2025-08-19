package com.bilty.generator.bridge

import com.bilty.generator.model.data.PrinterInfo
import com.bilty.generator.model.enums.PrintStatus
import org.apache.pdfbox.Loader
import org.apache.pdfbox.printing.PDFPrintable
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
        val printServices = PrintServiceLookup.lookupPrintServices(null, null)
        val selectedService: PrintService? = if (printerName != null) {
            printServices.find { it.name == printerName }
        } else {
            PrintServiceLookup.lookupDefaultPrintService()
        }

        if (selectedService == null) {
            println("❌ Error: Printer not found or no default printer is available.")
            return PrintStatus.FAILED
        }

        return try {
            // CORRECTED LINE: Use the new Loader class to load the PDF
            val document = Loader.loadPDF(data)

            val job = PrinterJob.getPrinterJob()
            job.printService = selectedService
            job.setPrintable(PDFPrintable(document))

            if (job.printDialog()) {
                job.print()
                PrintStatus.SUCCESS
            } else {
                PrintStatus.CANCELLED
            }
        } catch (e: Exception) {
            e.printStackTrace()
            PrintStatus.FAILED
        }
    }

}