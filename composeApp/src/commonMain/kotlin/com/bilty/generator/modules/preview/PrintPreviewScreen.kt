package com.bilty.generator.modules.preview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.bilty.generator.model.enums.PrintOption
import com.bilty.generator.model.enums.PrintOrientation
import com.bilty.generator.modules.AppRoutes
import com.bilty.generator.modules.preview.components.PreviewOptionsCard
import com.bilty.generator.modules.print.PrinterViewModel
import com.bilty.generator.uiToolKit.CommonDropdown
import com.bilty.generator.uiToolKit.CommonRadioGroup
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PrintPreviewScreen(navController: NavHostController) {
    val viewModel = remember { PrinterViewModel() }
    val scope = rememberCoroutineScope()

    val orientationsList by viewModel.orientationsList.collectAsState()
    val fontSizeList by viewModel.fontSizeList.collectAsState()
    val fontsList by viewModel.fontsList.collectAsState()

    val selectedFont by viewModel.selectedFonts.collectAsState()
    val selectedFontSize by viewModel.selectedFontSize.collectAsState()
    val selectedOrientation by viewModel.selectedOrientations.collectAsState()
    var selectedOption by remember { mutableStateOf<PrintOption?>(null) }

    fun onConfirm(printWithImage: Boolean) {
        if (getPlatform().name.contains(Constants.Platforms.PLATFORM_WEB) ||
            getPlatform().name.contains(Constants.Platforms.PLATFORM_DESKTOP)
        ) {
            navController.navigate(
                AppRoutes.PrinterScreen(
                    isPreviewWithImageBitmap = printWithImage,
                    fontSize = selectedFontSize,
                    isLandscapeMode = selectedOrientation == PrintOrientation.LANDSCAPE,
                    fontFamilyName = selectedFont

                )
            )
        } else {
            scope.launch {
                viewModel.setDefaultHtmlContentForPrint(isPreviewWithImageBitmap = printWithImage)
            }
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Receipt Previews",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            PreviewOptionsCard()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.Start
            ) {
                HorizontalDivider(
                    Modifier.padding(vertical = 10.dp),
                    DividerDefaults.Thickness,
                    DividerDefaults.color
                )

                // Title
                Text(
                    text = "Print Options",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 7.dp)
                )

                Text(
                    text = "Choose how you want to print your receipt:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Font Selection Dropdown
                    CommonDropdown(
                        label = "Select Font",
                        items = fontsList,
                        selectedItem = selectedFont,
                        onItemSelected = { viewModel.updateFontFamily(it) },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Font Size Dropdown
                    CommonDropdown(
                        label = "Font Size",
                        items = fontSizeList,
                        selectedItem = selectedFontSize.toString(),
                        onItemSelected = { viewModel.updateFontSize(it) },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // Orientation Radio Group
                CommonRadioGroup(
                    options = orientationsList,
                    selectedValue = selectedOrientation.value,
                    onOptionSelected = { viewModel.updateSelectedOrientation(it) },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Radio Button Options
                PrintOption.entries.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedOption = option }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = { selectedOption = option },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = option.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = option.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Confirm Button
                    Button(
                        onClick = {
                            selectedOption?.let { option ->
                                onConfirm(option == PrintOption.WITH_IMAGE)
                                selectedOption = null
                            }
                        },
                        enabled = selectedOption != null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirm")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}