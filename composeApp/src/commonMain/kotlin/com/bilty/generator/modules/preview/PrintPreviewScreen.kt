package com.bilty.generator.modules.preview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bilty.generator.modules.AppRoutes
import com.bilty.generator.uiToolKit.HtmlView
import com.bilty.generator.uiToolKit.generateRoadLineDeliveryReceipt
import com.bilty.generator.uiToolKit.getDemoRoadLineDeliveryReceipt

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PrintPreviewScreen(navController: NavHostController) {
    var showPreview by remember { mutableStateOf(true) }

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
                        navController.navigate(AppRoutes.PrinterScreen)
                        // In your Composable or ViewModel where you trigger the action
                        /*scope.launch {
                            // 1. Get an instance of the PDF generator
                            val pdfGenerator = getPdfGenerator()

                            // 2. Get an instance of the PrinterManager we created before
                            val printerManager = PrinterManager()

                            println("🚀 Starting PDF generation...")
                            // 3. Generate the PDF and get the data in memory
                            val pdfData: ByteArray? = pdfGenerator.generatePdf(
                                receipt = getDemoRoadLineDeliveryReceipt(),
                                isPreviewWithImageBitmap = false
                            )

                            // 4. Check if generation was successful
                            if (pdfData != null) {
                                println("✅ PDF generated successfully, now sending to printer...")

                                // 5. Pass the ByteArray to the printer
                                val printStatus = printerManager.printPdf(pdfData)

                                println("🖨️ Print job status: $printStatus")
                                // You can show a message to the user based on the status

                            } else {
                                println("❌ PDF generation failed.")
                            }
                        }*/
                    }) {
                        Text("Print Receipt")
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
                    horizontalArrangement = Arrangement.spacedBy(
                        16.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Preview 1: Text Only
                    Card(
                        modifier = Modifier.width(945.dp).height(630.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Image Version", fontSize = 12.sp, color = Color.Gray)
                            Spacer(Modifier.height(4.dp))
                            // The background color helps confirm the composable is being laid out.
                            HtmlView(
                                html = receiptHtmlContentWithImage,
                                modifier = Modifier.fillMaxSize()
                                    .background(Color.LightGray.copy(alpha = 0.2f))
                            )
                        }
                    }

                    // Preview 2: With Image
                    Card(
                        modifier = Modifier.width(945.dp).height(630.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("v", fontSize = 12.sp, color = Color.Gray)
                            Spacer(Modifier.height(4.dp))
                            HtmlView(
                                html = receiptHtmlContent,
                                modifier = Modifier.fillMaxSize()
                                    .background(Color.LightGray.copy(alpha = 0.2f))
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
