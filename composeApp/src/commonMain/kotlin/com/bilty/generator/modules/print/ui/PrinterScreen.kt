package com.bilty.generator.modules.print.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import biltygenerator.composeapp.generated.resources.Res
import biltygenerator.composeapp.generated.resources.button_print_pdf
import biltygenerator.composeapp.generated.resources.button_print_text
import biltygenerator.composeapp.generated.resources.cd_print_icon
import biltygenerator.composeapp.generated.resources.message_generating_pdf
import biltygenerator.composeapp.generated.resources.title_select_printer
import com.bilty.generator.bridge.getPdfGenerator
import com.bilty.generator.model.enums.PrintOrientation
import com.bilty.generator.modules.print.components.EmptyPrinterListView
import com.bilty.generator.modules.print.components.PrinterRow
import com.bilty.generator.uiToolKit.PrintingStatusBottomSheet
import com.bilty.generator.uiToolKit.getDemoRoadLineDeliveryReceipt
import com.bilty.generator.uiToolKit.getHtmlPageZoomLevel
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterScreen(
    navController: NavHostController,
    isPreviewWithImageBitmap: Boolean,
    fontSize: Int,
    isLandscapeMode: Boolean,
    fontFamilyName: String
) {
    val viewModel = PrinterViewModel()
    val uiState by viewModel.uiState.collectAsState()
    var receiptHtmlByteArray by remember { mutableStateOf<ByteArray?>(null) }
    var isPdfGenerating by remember { mutableStateOf(isPreviewWithImageBitmap) }

    LaunchedEffect(Unit) {
        viewModel.updateFontSize(size = fontSize.toString())
        viewModel.updateSelectedOrientation(
            orientation = if (isLandscapeMode) PrintOrientation.LANDSCAPE.value
            else PrintOrientation.PORTRAIT.value
        )
        viewModel.updateFontFamily(fontFamily = fontFamilyName)
    }

    LaunchedEffect(Unit) {
        // Only generate PDF if using PDF print method
        if (isPreviewWithImageBitmap) {
            try {
                println("Starting PDF generation...")
                val pdfData = getPdfGenerator().generatePdf(
                    receipt = getDemoRoadLineDeliveryReceipt(),
                    isPreviewWithImageBitmap = isPreviewWithImageBitmap,
                    isWantToSavePDFLocally = true, // Save PDF to disk before printing
                    zoomLevel = getHtmlPageZoomLevel(),
                    fontSize = fontSize,
                    isLandscapeMode = isLandscapeMode,
                    fontFamilyName = viewModel.returnFontStyleByName(fontFamilyName)
                )
                receiptHtmlByteArray = pdfData
                isPdfGenerating = false
                println("PDF generation completed and saved: ${pdfData?.size ?: 0} bytes")
            } catch (e: Exception) {
                println("PDF generation failed in LaunchedEffect: ${e.message}")
                e.printStackTrace()
                isPdfGenerating = false
            }
        } else {
            println("Direct text print mode - skipping PDF generation")
            isPdfGenerating = false
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(Res.string.title_select_printer)) }, actions = {
                Button(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
                }
            })
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(80.dp)
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    itemVerticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        enabled = !isPdfGenerating,
                        onClick = {
                            if (isPreviewWithImageBitmap) {
                                // PDF Print Mode
                                receiptHtmlByteArray?.let { pdfData ->
                                    if (pdfData.isNotEmpty()) {
                                        println("🖨️ Print button clicked with PDF data: ${pdfData.size} bytes")
                                        viewModel.onPrintClicked(pdfData = pdfData)
                                    } else {
                                        println("❌ Print button clicked but PDF data is empty")
                                    }
                                } ?: run {
                                    println("❌ Print button clicked but PDF data is null")
                                }
                            } else {
                                // Direct Text Print Mode
                                println("🖨️ Print button clicked for direct text print")
                                viewModel.onDirectTextPrintClicked()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        if (isPdfGenerating) {
                            CircularProgressIndicator()
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(Res.string.message_generating_pdf))
                        } else {
                            Icon(
                                Icons.Default.Print,
                                contentDescription = stringResource(Res.string.cd_print_icon)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (isPreviewWithImageBitmap) stringResource(Res.string.button_print_pdf) else stringResource(
                                    Res.string.button_print_text
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.printers.isEmpty()) {
                EmptyPrinterListView()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(uiState.printers) { printer ->
                        PrinterRow(
                            printerName = printer.name,
                            isSelected = printer.name == uiState.selectedPrinterName,
                            onSelected = { viewModel.onPrinterSelected(printer.name) }
                        )
                        HorizontalDivider(
                            Modifier,
                            DividerDefaults.Thickness,
                            DividerDefaults.color
                        )
                    }
                }
            }
        }

        // Printing bottom sheet (modal, blocks until done)
        PrintingStatusBottomSheet(
            isVisible = uiState.isPrinting || uiState.lastPrintStatus != null,
            progressPercent = uiState.printProgress,
            statusText = uiState.printStatusMessage,
            isCompleted = uiState.lastPrintStatus != null,
            onDismissBlocked = { /* block dismiss */ },
            onCloseAfterComplete = { viewModel.resetPrintStatus() }
        )
    }
}