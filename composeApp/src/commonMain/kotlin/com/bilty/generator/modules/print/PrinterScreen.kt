package com.bilty.generator.modules.print

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bilty.generator.model.data.PrinterInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterScreen(navController: NavHostController) {
    val viewModel = PrinterViewModel()
    val uiState by viewModel.uiState.collectAsState()

    // This is where you would get your actual PDF data from your PdfGenerator
    // For this example, we'll use a placeholder.
    // val dummyPdfData: ByteArray = "This is a test PDF".encodeToByteArray()
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
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
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
                PrinterList(
                    printers = uiState.printers,
                    selectedPrinterName = uiState.selectedPrinterName,
                    onPrinterSelected = viewModel::onPrinterSelected
                )
            }
        }
    }
}

@Composable
private fun PrinterList(
    printers: List<PrinterInfo>,
    selectedPrinterName: String?,
    onPrinterSelected: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(printers) { printer ->
            PrinterRow(
                printerName = printer.name,
                isSelected = printer.name == selectedPrinterName,
                onSelected = { onPrinterSelected(printer.name) }
            )
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        }
    }
}

@Composable
private fun PrinterRow(
    printerName: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelected)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelected
        )
        Spacer(Modifier.width(16.dp))
        Text(text = printerName, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun EmptyPrinterListView() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Printers Found",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "On this platform, the printer list is shown in the system's print dialog after you click 'Print'.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
    }
}