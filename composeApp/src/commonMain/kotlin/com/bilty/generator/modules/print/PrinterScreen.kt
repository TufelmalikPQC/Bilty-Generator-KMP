package com.bilty.generator.modules.print

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bilty.generator.modules.print.components.EmptyPrinterListView
import com.bilty.generator.modules.print.components.PrinterRow
import com.bilty.generator.uiToolKit.PrintingStatusBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterScreen(navController: NavHostController, isPreviewWithImageBitmap: Boolean) {
    println("isPreviewWithImageBitmap => $isPreviewWithImageBitmap")
    val viewModel = PrinterViewModel()
    val uiState by viewModel.uiState.collectAsState()
    var isPrintDotMatrixFormat by remember { mutableStateOf(true) }

    val defaultPdfByteArray by viewModel.defaultPdfData.collectAsState()


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Select a Printer") }, actions = {
                Button(onClick = { viewModel.onPrintClicked(pdfData = defaultPdfByteArray) }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, "",
                        modifier = Modifier.clickable {
                            navController.popBackStack()
                        })
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
                        onClick = { viewModel.onPrintClicked(pdfData = defaultPdfByteArray) },
                        // Button is enabled only when a printer is selected or if the list is empty
                        // (for platforms like Android where the system dialog handles selection)
                        enabled = uiState.selectedPrinterName != null || uiState.printers.isEmpty(),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = "Print Icon")
                        Spacer(Modifier.width(8.dp))
                        Text("PRINT DOCUMENT")
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
                    item {
                        FlowRow(
                            itemVerticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                text = "Print Dot Matrix Format"
                            )
                            Spacer(Modifier.width(2.dp))
                            Checkbox(
                                colors = CheckboxDefaults.colors(),
                                checked = isPrintDotMatrixFormat,
                                onCheckedChange = { isPrintDotMatrixFormat = it },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
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