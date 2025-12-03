package com.bilty.generator.modules.remotePrint.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bilty.generator.model.data.GrMaster
import com.bilty.generator.theme.ThemeColors


@Composable
fun GrMasterItem(
    gr: GrMaster,
    isSelected: Boolean = false,
    onSelected: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(
                if (isSelected) ThemeColors.printRequestPrimaryColor.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.surface,
                RoundedCornerShape(8.dp)
            )
            .clickable { onSelected() }
            .padding(14.dp)
    ) {

        // ===== ALWAYS SHOWN ====
        Text(
            text = gr.grInfoId.toString(),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Rate: ${gr.rateType}",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Sender: ${gr.senderId}",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Receiver: ${gr.receiverId}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))


        // ===== EXPANDED BLOCK =====
        AnimatedVisibility(expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

                LabelValue("Destination Branch", gr.destinationBranchId)
                LabelValue("Destination Location", gr.destinationLocation)
                LabelValue("Current Location Branch", gr.currentLocationBranchId)
                LabelValue("Booking Date", gr.bookingDate)
                LabelValue("Delivery Date", gr.deliveryDate)
                LabelValue("Delivery Type", gr.deliveryTypeId)
                LabelValue("Private Mark", gr.privateMark)
                LabelValue("Remarks", gr.remarks)
                LabelValue("POD Required", gr.isPodRequired)
                LabelValue("POD Status", gr.podStatusId)
                LabelValue("Created By", gr.createdBy)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ===== EXPAND/COLLAPSE BUTTON =====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (expanded) "See Less" else "See More",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                else Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun LabelValue(label: String, value: Any?) {
    if (value == null) return
    if (value.toString().isBlank()) return

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}