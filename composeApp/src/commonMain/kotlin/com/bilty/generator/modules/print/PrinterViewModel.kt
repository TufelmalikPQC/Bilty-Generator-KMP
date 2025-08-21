package com.bilty.generator.modules.print


import com.bilty.generator.bridge.PrinterManager
import com.bilty.generator.bridge.getPdfGenerator
import com.bilty.generator.model.data.PrinterScreenUiState
import com.bilty.generator.model.enums.PrintStatus
import com.bilty.generator.uiToolKit.getDemoRoadLineDeliveryReceipt
import com.bilty.generator.uiToolKit.getHtmlPageZoomLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    fun resetPrintStatus() {
        _uiState.update {
            it.copy(
                isPrinting = false,
                printProgress = 0,
                printStatusMessage = "",
                lastPrintStatus = null
            )
        }
    }

    init {
        loadPrinters()
//        loadDefaultPDFData()
    }

    private fun loadDefaultPDFData() {
        viewModelScope.launch {
            try {
                println("🔄 Loading default PDF data...")
                val pdfData = getPdfGenerator().generatePdf(
                    receipt = getDemoRoadLineDeliveryReceipt(),
                    isPreviewWithImageBitmap = false,
                    isWantToSavePDFLocally = false,
                    zoomLevel = getHtmlPageZoomLevel()
                )

                if (pdfData?.isNotEmpty() == true) {
                    println("✅ Default PDF data loaded successfully: ${pdfData.size} bytes")
                    defaultPdfData.emit(pdfData)
                } else {
                    println("❌ Failed to load default PDF data - received null or empty data")
                    defaultPdfData.emit(ByteArray(0))
                }
            } catch (e: Exception) {
                println("❌ Error loading default PDF data: ${e.message}")
                e.printStackTrace()
                defaultPdfData.emit(ByteArray(0))
            }
        }
    }

    fun onPrinterSelected(printerName: String) {
        _uiState.update { it.copy(selectedPrinterName = printerName) }
    }

    fun onPrintClicked(pdfData: ByteArray) {
        viewModelScope.launch {
            val selectedPrinter = _uiState.value.selectedPrinterName
            println("📄 Received PDF data for printing: ${pdfData.size} bytes")
            println("🖨️ Sending print job to '${selectedPrinter ?: "default printer"}'...")

            // Validate PDF data before proceeding
            if (pdfData.isEmpty()) {
                println("❌ Error: PDF data is empty, cannot proceed with printing")
                _uiState.update {
                    it.copy(
                        isPrinting = false,
                        printProgress = 0,
                        printStatusMessage = "Error: PDF data is empty",
                        lastPrintStatus = PrintStatus.FAILED
                    )
                }
                return@launch
            }

            // Show printing bottom sheet
            _uiState.update {
                it.copy(
                    isPrinting = true,
                    printProgress = 0,
                    printStatusMessage = "Preparing print job...",
                    lastPrintStatus = null
                )
            }

            // Simulated staged progress while waiting for actual print result
            launch { simulateProgressWhilePrinting() }

            // Actual print call
            val status = printerManager.printPdf(pdfData, selectedPrinter)

            // Update final state
            _uiState.update {
                it.copy(
                    isPrinting = false,
                    printProgress = if (status == PrintStatus.SUCCESS) 100 else it.printProgress,
                    printStatusMessage = when (status) {
                        PrintStatus.SUCCESS -> "Printed successfully."
                        PrintStatus.CANCELLED -> "Print cancelled."
                        PrintStatus.FAILED -> "Print failed."
                        PrintStatus.PENDING -> "Print pending."
                        PrintStatus.NOT_SUPPORTED -> "Printing not supported on this platform."
                        PrintStatus.NOT_STARTED -> "Not Started"
                    },
                    lastPrintStatus = status
                )
            }

            println("✅ Print job status: $status")
        }
    }

    private suspend fun simulateProgressWhilePrinting() {
        // Increase progress gradually until 90% (so we can set 100% on success)
        for (p in 0..90 step 5) {
            delay(200)
            _uiState.update {
                // Stop updating if printing completed
                if (!it.isPrinting) return
                it.copy(
                    printProgress = p,
                    printStatusMessage = when {
                        p < 20 -> "Preparing print job..."
                        p < 50 -> "Generating document..."
                        p < 80 -> "Sending to printer..."
                        else -> "Waiting for printer..."
                    }
                )
            }
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

    fun setDefaultHtmlContentForPrint(isPreviewWithImageBitmap: Boolean) {
        viewModelScope.launch {
            try {
                val pdfData = getPdfGenerator().generatePdf(
                    receipt = getDemoRoadLineDeliveryReceipt(),
                    isPreviewWithImageBitmap = isPreviewWithImageBitmap,
                    isWantToSavePDFLocally = false,
                    zoomLevel = getHtmlPageZoomLevel()
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