package com.bilty.generator.modules.preview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bilty.generator.bridge.getPdfGenerator
import com.bilty.generator.uiToolKit.HtmlView
import com.bilty.generator.uiToolKit.generateRoadLineDeliveryReceipt
import com.bilty.generator.uiToolKit.getDemoRoadLineDeliveryReceipt
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PrintPreviewScreen() {
    var showPreview by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // Generate the HTML content from your logic.
    val receiptHtmlContent = remember {
        generateRoadLineDeliveryReceipt(
            getDemoRoadLineDeliveryReceipt(),
            isPreviewWithImageBitmap = false
        )
    }
    val receiptHtmlContentWithImage = remember {
        generateRoadLineDeliveryReceipt(
            getDemoRoadLineDeliveryReceipt(),
            isPreviewWithImageBitmap = true
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("KMM PDF Preview") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        12.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { showPreview = !showPreview }) {
                        Text(if (showPreview) "Hide Preview" else "Show Preview")
                    }
                    Button(onClick = {
                        scope.launch {
                            val pdfGenerator = getPdfGenerator()
                            val pdfSuccess = pdfGenerator.generatePdf(
                                receipt = getDemoRoadLineDeliveryReceipt(),
                                isPreviewWithImageBitmap = true // Assuming you want the image version for PDF
                            )
                            if (pdfSuccess) {
                                println("PDF generated successfully")
                            } else {
                                println("PDF generation failed")
                            }
                        }
                    }) {
                        Text("Print PDF")
                    }
                    OutlinedButton(onClick = {
                        println("--- GENERATED HTML (TEXT) ---")
                        println(receiptHtmlContent)
                        println("--- GENERATED HTML (IMAGE) ---")
                        println(receiptHtmlContentWithImage)
                    }) {
                        Text("Log HTML")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showPreview) {
                Text(
                    text = "Showing Previews:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                // Using FlowRow to allow previews to wrap on smaller screens if necessary
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Preview 1: Text Only
                    Card(
                        modifier = Modifier.width(945.dp).height(630.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Text-Only Version", fontSize = 12.sp, color = Color.Gray)
                            Spacer(Modifier.height(4.dp))
                            // The background color helps confirm the composable is being laid out.
                            HtmlView(
                                html = receiptHtmlContent,
                                modifier = Modifier.fillMaxSize().background(Color.LightGray.copy(alpha = 0.2f))
                            )
                        }
                    }

                    // Preview 2: With Image
                    Card(
                        modifier = Modifier.width(945.dp).height(630.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Image Version", fontSize = 12.sp, color = Color.Gray)
                            Spacer(Modifier.height(4.dp))
                            HtmlView(
                                html = receiptHtmlContentWithImage,
                                modifier = Modifier.fillMaxSize().background(Color.LightGray.copy(alpha = 0.2f))
                            )
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Preview is hidden.")
                }
            }
        }
    }
}
