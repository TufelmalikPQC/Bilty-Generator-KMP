package com.bilty.generator.modules.print.ui


import androidx.lifecycle.ViewModel
import com.bilty.generator.bridge.PrinterManager
import com.bilty.generator.bridge.getPdfGenerator
import com.bilty.generator.model.data.PrinterScreenUiState
import com.bilty.generator.model.enums.FontStyles
import com.bilty.generator.model.enums.PrintOrientation
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


class PrinterViewModel : ViewModel() {
    // This is the PrinterManager we've been building
    private val printerManager = PrinterManager()
    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    private val _uiState = MutableStateFlow(PrinterScreenUiState())
    val uiState = _uiState.asStateFlow()


    var defaultPdfData = MutableStateFlow(ByteArray(0))
        private set

    var orientationsList = MutableStateFlow(listOf<PrintOrientation>())
        private set

    var selectedOrientations = MutableStateFlow(PrintOrientation.LANDSCAPE)
        private set

    var fontSizeList = MutableStateFlow(listOf<String>())
        private set

    var selectedFontSize = MutableStateFlow(12)
        private set

    var fontsList = MutableStateFlow(listOf<FontStyles>())
        private set

    var selectedFonts = MutableStateFlow(FontStyles.DOT_MATRIX)
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
        loadFontsList()
        loadOrientationList()
        loadFontSizeList()
    }

    fun onPrinterSelected(printerName: String) {
        _uiState.update { it.copy(selectedPrinterName = printerName) }
    }

    fun onDirectTextPrintClicked() {
        viewModelScope.launch {
            val selectedPrinter = _uiState.value.selectedPrinterName
            println("📄 Direct text print mode")
            println("🖨️ Sending text print job to '${selectedPrinter ?: "default printer"}'...")

            // Show printing bottom sheet
            _uiState.update {
                it.copy(
                    isPrinting = true,
                    printProgress = 0,
                    printStatusMessage = "Preparing text print job...",
                    lastPrintStatus = null
                )
            }

            // Simulated staged progress while waiting for actual print result
            launch { simulateProgressWhilePrinting() }

            // Direct text print call
            val status = printerManager.printBiltyTextFile(
                printerName = selectedPrinter,
                fontSize = selectedFontSize.value,
                isLandscapeMode = selectedOrientations.value.value == "LANDSCAPE",
                fontFamilyName = selectedFonts.value
            )

            // Update final state
            _uiState.update {
                it.copy(
                    isPrinting = false,
                    printProgress = if (status == PrintStatus.COMPLETED) 100 else it.printProgress,
                    printStatusMessage = when (status) {
                        PrintStatus.COMPLETED -> "Text printed successfully."
                        PrintStatus.CANCELLED -> "Print cancelled."
                        PrintStatus.FAILED -> "Print failed."
                        PrintStatus.PENDING -> "Print pending."
                        PrintStatus.NOT_SUPPORTED -> "Printing not supported on this platform."
                        PrintStatus.NOT_STARTED -> "Not Started"
                        PrintStatus.PRINTING -> "Printing in progress..."
                    },
                    lastPrintStatus = status
                )
            }

            println("✅ Text print job status: $status")
        }
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

            // Actual print call with PDF
            val status = printerManager.printPdf(
                pdfContentData = pdfData,
                printerName = selectedPrinter,
                fontSize = selectedFontSize.value,
                isLandscapeMode = selectedOrientations.value.value == "LANDSCAPE",
                fontFamilyName = selectedFonts.value
            )

            // Update final state
            _uiState.update {
                it.copy(
                    isPrinting = false,
                    printProgress = if (status == PrintStatus.COMPLETED) 100 else it.printProgress,
                    printStatusMessage = when (status) {
                        PrintStatus.COMPLETED -> "PDF printed successfully."
                        PrintStatus.CANCELLED -> "Print cancelled."
                        PrintStatus.FAILED -> "Print failed."
                        PrintStatus.PENDING -> "Print pending."
                        PrintStatus.NOT_SUPPORTED -> "Printing not supported on this platform."
                        PrintStatus.NOT_STARTED -> "Not Started"
                        PrintStatus.PRINTING -> "Printing in progress..."
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
                    zoomLevel = getHtmlPageZoomLevel(),
                    fontSize = selectedFontSize.value,
                    isLandscapeMode = selectedOrientations.value == PrintOrientation.LANDSCAPE,
                    fontFamilyName = selectedFonts.value
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

    fun loadOrientationList() {
        viewModelScope.launch {
            orientationsList.emit(
                listOf(
                    PrintOrientation.LANDSCAPE,
                    PrintOrientation.PORTRAIT
                )
            )
        }
    }

    fun loadFontsList() {
        viewModelScope.launch {
            fontsList.emit(
                listOf(
                    FontStyles.DOT_MATRIX,
                    FontStyles.ENHANCED_DOT_MATRIX,
                    FontStyles.DIGI_TRACE,
                    FontStyles.DOT_DIGITAL_7,
                    FontStyles.DIGITAL_7
                )
            )
        }
    }

    fun loadFontSizeList() {
        viewModelScope.launch {
            fontSizeList.emit((12..60 step 2).map { it.toString() })
        }
    }

    fun updateFontSize(size: String) {
        viewModelScope.launch {
            selectedFontSize.emit(size.toInt())
        }
    }

    fun updateFontFamily(fontFamily: String) {
        viewModelScope.launch {
            selectedFonts.emit(returnFontStyleByName(fontFamily))
        }
    }

    fun updateSelectedOrientation(orientation: String) {
        viewModelScope.launch {
            val matched = orientationsList.value.find { it.value == orientation }
            if (matched != null) {
                selectedOrientations.emit(matched)
            }
        }
    }

    fun returnFontStyleByName(style: String): FontStyles {
        return when (style) {
            FontStyles.DOT_MATRIX.name -> FontStyles.DOT_MATRIX
            FontStyles.ENHANCED_DOT_MATRIX.name -> FontStyles.ENHANCED_DOT_MATRIX
            FontStyles.DIGI_TRACE.name -> FontStyles.DIGI_TRACE
            FontStyles.DOT_DIGITAL_7.name -> FontStyles.DOT_DIGITAL_7
            else -> FontStyles.ROBOTO
        }
    }

}