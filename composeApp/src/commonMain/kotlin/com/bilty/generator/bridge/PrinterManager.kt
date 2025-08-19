package com.bilty.generator.bridge

import com.bilty.generator.model.PrintData
import com.bilty.generator.model.data.PrinterInfo
import com.bilty.generator.model.enums.PrintStatus

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class PrinterManager() {
    /**
     * Gets a list of printers available to the system.
     * Note: This will return an empty list on Web, iOS, and Android
     * because those platforms only show printers in the system's print dialog.
     *
     * @return A list of [PrinterInfo] objects, primarily for desktop use.
     */
    fun getAvailablePrinters(): List<PrinterInfo>

    /**
     * Prints a PDF document from its raw data in a ByteArray.
     * This is useful for printing PDFs generated in memory.
     *
     * @param data The ByteArray of the PDF file.
     * @param printerName The name of a specific printer to use (Desktop only). If null, a default is used.
     * @return A [PrintStatus] indicating the outcome.
     */
//    suspend fun printPdf(data: ByteArray, printerName: String? = null): PrintStatus

}