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
import biltygenerator.composeapp.generated.resources.Res
import biltygenerator.composeapp.generated.resources.str_view_raw_receipt
import biltygenerator.composeapp.generated.resources.str_view_raw_receipt_desc
import biltygenerator.composeapp.generated.resources.str_view_receipt_with_image
import biltygenerator.composeapp.generated.resources.str_view_receipt_with_image_desc
import com.bilty.generator.bridge.openUrlInBrowser
import com.bilty.generator.model.data.PreviewOptionData
import com.bilty.generator.uiToolKit.generateRoadLineDeliveryReceipt
import com.bilty.generator.uiToolKit.getDemoRoadLineDeliveryReceipt
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun PreviewOptionsCard() {
    val scope = rememberCoroutineScope()

    val options = listOf(
        PreviewOptionData(
            title = stringResource(Res.string.str_view_receipt_with_image),
            description = stringResource(Res.string.str_view_receipt_with_image_desc),
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
            title = stringResource(Res.string.str_view_raw_receipt),
            description = stringResource(Res.string.str_view_raw_receipt_desc),
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
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        PreviewOptionsGrid(options = options)
    }
}