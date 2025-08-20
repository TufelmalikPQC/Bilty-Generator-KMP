package com.bilty.generator.modules.print


import com.bilty.generator.bridge.PrinterManager
import com.bilty.generator.bridge.getPdfGenerator
import com.bilty.generator.model.data.PrinterScreenUiState
import com.bilty.generator.uiToolKit.getDemoRoadLineDeliveryReceipt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class PrinterViewModel {
    // This is the PrinterManager we've been building
    private val printerManager = PrinterManager()
    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    private val _uiState = MutableStateFlow(PrinterScreenUiState())
    val uiState = _uiState.asStateFlow()


    var defaultPdfData = MutableStateFlow(ByteArray(0))
        private set


    init {
        loadPrinters()
        loadDefaultPDFData()
    }

    private fun loadDefaultPDFData() {
        viewModelScope.launch {
            defaultPdfData.emit(
                getPdfGenerator().generatePdf(
                    receipt = getDemoRoadLineDeliveryReceipt(),
                    isPreviewWithImageBitmap = false
                ) ?: ByteArray(0)
            )
        }
    }

    fun onPrinterSelected(printerName: String) {
        _uiState.update { it.copy(selectedPrinterName = printerName) }
    }

    fun onPrintClicked(pdfData: ByteArray) {
        viewModelScope.launch {
            val selectedPrinter = _uiState.value.selectedPrinterName
            println("🖨️ Sending print job to '${selectedPrinter ?: "default printer"}'...")

            val status = printerManager.printPdf(pdfData, selectedPrinter)

            println("✅ Print job status: $status")
            // Here you could show a Toast or Snackbar with the result
        }
    }

    private fun loadPrinters() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val availablePrinters = printerManager.getAvailablePrinters()
            val defaultPrinter = availablePrinters.firstOrNull { it.isDefault }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    printers = availablePrinters,
                    // Pre-select the default printer if one exists
                    selectedPrinterName = defaultPrinter?.name
                )
            }
        }
    }


    suspend fun setDefaultHtmlContentForPrint(isPreviewWithImageBitmap: Boolean) {
        viewModelScope.launch {
            try {
                val pdfData = getPdfGenerator().generatePdf(
                    receipt = getDemoRoadLineDeliveryReceipt(),
                    isPreviewWithImageBitmap = isPreviewWithImageBitmap
                )

                if (pdfData != null && pdfData.isNotEmpty()) {
                    println("📄 PDF generated successfully: ${pdfData.size} bytes")
                    defaultPdfData.emit(pdfData)
                    // Trigger printing
                    onPrintClicked(pdfData)
                } else {
                    println("❌ Failed to generate PDF")
                    // Handle error - maybe show a toast or error message
                }
            } catch (e: Exception) {
                println("❌ Error in setDefaultHtmlContentForPrint: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}