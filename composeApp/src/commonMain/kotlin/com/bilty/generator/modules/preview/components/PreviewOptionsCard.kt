package com.bilty.generator.modules.preview.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bilty.generator.bridge.openUrlInBrowser
import com.bilty.generator.model.data.PreviewOptionData
import com.bilty.generator.uiToolKit.generateRoadLineDeliveryReceipt
import com.bilty.generator.uiToolKit.getDemoRoadLineDeliveryReceipt
import kotlinx.coroutines.launch

@Composable
fun PreviewOptionsCard() {
    val scope = rememberCoroutineScope()

    val options = listOf(
        PreviewOptionData(
            title = "View receipt with background image",
            description = "Opens a styled preview of the receipt with branding",
            icon = Icons.Default.Image,
            onClick = {
                scope.launch {
                    openUrlInBrowser(
                        generateRoadLineDeliveryReceipt(
                            getDemoRoadLineDeliveryReceipt(),
                            isPreviewWithImageBitmap = true,
                            false
                        )
                    )
                }
            }
        ),
        PreviewOptionData(
            title = "View raw receipt data",
            description = "See the plain text receipt information",
            icon = Icons.Default.Description,
            onClick = {
                scope.launch {
                    openUrlInBrowser(
                        generateRoadLineDeliveryReceipt(
                            getDemoRoadLineDeliveryReceipt(),
                            isPreviewWithImageBitmap = false,
                            false
                        )
                    )
                }
            }
        )
        // You can add more PreviewOptionData items here if needed
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        PreviewOptionsGrid(options = options)
    }
}