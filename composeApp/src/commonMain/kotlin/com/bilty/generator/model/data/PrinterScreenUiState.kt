package com.bilty.generator.model.data

data class PrinterScreenUiState(
    val isLoading: Boolean = true,
    val printers: List<PrinterInfo> = emptyList(),
    val selectedPrinterName: String? = null,
    val isPrintDotMatrixFormat: Boolean = false
)