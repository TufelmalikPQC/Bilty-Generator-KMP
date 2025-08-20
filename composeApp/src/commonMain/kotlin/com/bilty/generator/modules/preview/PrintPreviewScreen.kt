package com.bilty.generator.modules.preview


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bilty.generator.getPlatform
import com.bilty.generator.model.constants.Constants
import com.bilty.generator.modules.AppRoutes
import com.bilty.generator.modules.preview.componetnts.PrintPreviewCard
import com.bilty.generator.modules.print.PrinterViewModel
import com.bilty.generator.uiToolKit.PreviewTabs
import com.bilty.generator.uiToolKit.PrintOptionsBottomSheet
import com.bilty.generator.uiToolKit.generateRoadLineDeliveryReceipt
import com.bilty.generator.uiToolKit.getDemoRoadLineDeliveryReceipt
import com.bilty.generator.uiToolKit.getHtmlPageZoomLevel
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PrintPreviewScreen(navController: NavHostController) {
    var showPreview by remember { mutableStateOf(true) }
    var receiptHtmlContent by remember { mutableStateOf<String?>(null) }
    var receiptHtmlContentWithImage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    println("Platform: ${getPlatform().name}")
    val viewModel = PrinterViewModel()
    val scope = rememberCoroutineScope()
    var showPrintOptions by remember { mutableStateOf(false) }

    // Load HTML content with proper error handling
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            error = null

            receiptHtmlContent = generateRoadLineDeliveryReceipt(
                getDemoRoadLineDeliveryReceipt(),
                isPreviewWithImageBitmap = false,
                zoomLevel = getHtmlPageZoomLevel()
            )

            receiptHtmlContentWithImage = generateRoadLineDeliveryReceipt(
                getDemoRoadLineDeliveryReceipt(),
                isPreviewWithImageBitmap = true,
                zoomLevel = getHtmlPageZoomLevel()
            )
        } catch (e: Exception) {
            error = e.message ?: "Unknown error occurred"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Receipt Preview") },
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        12.dp,
                        Alignment.CenterHorizontally
                    )
                ) {
                    // Toggle Preview Button
                    Button(
                        onClick = { showPreview = !showPreview },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (showPreview) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (showPreview) "Hide Preview" else "Show Preview")
                    }

                    // Print Button
                    Button(
                        onClick = { showPrintOptions = true },
                        enabled = !isLoading && error == null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
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
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading receipt templates...")
                        }
                    }
                }

                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error: $error",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    // Retry loading
                                    scope.launch {
                                        try {
                                            isLoading = true
                                            error = null

                                            receiptHtmlContent = generateRoadLineDeliveryReceipt(
                                                getDemoRoadLineDeliveryReceipt(),
                                                isPreviewWithImageBitmap = false
                                            )

                                            receiptHtmlContentWithImage =
                                                generateRoadLineDeliveryReceipt(
                                                    getDemoRoadLineDeliveryReceipt(),
                                                    isPreviewWithImageBitmap = true
                                                )
                                        } catch (e: Exception) {
                                            error = e.message ?: "Unknown error occurred"
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }

                showPreview -> {
                    Text(
                        text = "Receipt Previews",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Preview Cards in Column for better layout
                    Column(
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        PreviewTabs(
                            htmlContentWithImage = {
                                // Preview 1: With Background Image
                                PrintPreviewCard(
                                    title = "With Background Image",
                                    description = "Official receipt with template background",
                                    htmlContent = receiptHtmlContentWithImage
                                )
                            },
                            htmlContentWithoutImage = {
                                // Preview 2: Content Only
                                PrintPreviewCard(
                                    title = "Content Only",
                                    description = "Clean text-only version for faster printing",
                                    htmlContent = receiptHtmlContent
                                )
                            }
                        )
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Preview is hidden",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Click 'Show Preview' to view receipt templates",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Print Options Bottom Sheet
    PrintOptionsBottomSheet(
        isVisible = showPrintOptions,
        onDismiss = { showPrintOptions = false },
        onConfirm = { printWithImage ->
            showPrintOptions = false

            if (getPlatform().name.contains(Constants.Platforms.PLATFORM_WEB) ||
                getPlatform().name.contains(Constants.Platforms.PLATFORM_DESKTOP)
            ) {
                navController.navigate(AppRoutes.PrinterScreen(isPreviewWithImageBitmap = printWithImage))
            } else {
                scope.launch {
                    viewModel.setDefaultHtmlContentForPrint(isPreviewWithImageBitmap = printWithImage)
                }
            }
        }
    )
}
