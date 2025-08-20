package com.bilty.generator.model.data

import com.bilty.generator.model.enums.PrintStatus

/**
 * UI state for the Printer screen
 */
data class PrinterScreenUiState(
    val isLoading: Boolean = true,
    val printers: List<PrinterInfo> = emptyList(),
    val selectedPrinterName: String? = null,
    val isPrintDotMatrixFormat: Boolean = false,

    // Printing status UI
    val isPrinting: Boolean = false,
    val printProgress: Int = 0, // 0..100
    val printStatusMessage: String = "",
    val lastPrintStatus: PrintStatus? = null
)