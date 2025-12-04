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
import biltygenerator.composeapp.generated.resources.Res
import biltygenerator.composeapp.generated.resources.cd_expand_less
import biltygenerator.composeapp.generated.resources.cd_expand_more
import biltygenerator.composeapp.generated.resources.label_booking_date
import biltygenerator.composeapp.generated.resources.label_created_by
import biltygenerator.composeapp.generated.resources.label_current_location_branch
import biltygenerator.composeapp.generated.resources.label_delivery_date
import biltygenerator.composeapp.generated.resources.label_delivery_type
import biltygenerator.composeapp.generated.resources.label_destination_branch
import biltygenerator.composeapp.generated.resources.label_destination_location
import biltygenerator.composeapp.generated.resources.label_pod_required
import biltygenerator.composeapp.generated.resources.label_pod_status
import biltygenerator.composeapp.generated.resources.label_private_mark
import biltygenerator.composeapp.generated.resources.label_rate_prefix
import biltygenerator.composeapp.generated.resources.label_receiver_prefix
import biltygenerator.composeapp.generated.resources.label_remarks
import biltygenerator.composeapp.generated.resources.label_see_less
import biltygenerator.composeapp.generated.resources.label_see_more
import biltygenerator.composeapp.generated.resources.label_sender_prefix
import com.bilty.generator.model.data.GrMaster
import com.bilty.generator.theme.ThemeColors
import org.jetbrains.compose.resources.stringResource


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
            text = stringResource(Res.string.label_rate_prefix, gr.rateType.toString()),
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = stringResource(Res.string.label_sender_prefix, gr.senderId.toString()),
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = stringResource(Res.string.label_receiver_prefix, gr.receiverId.toString()),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))


        // ===== EXPANDED BLOCK =====
        AnimatedVisibility(expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                LabelValue(stringResource(Res.string.label_destination_branch), gr.destinationBranchId)
                LabelValue(stringResource(Res.string.label_destination_location), gr.destinationLocation)
                LabelValue(stringResource(Res.string.label_current_location_branch), gr.currentLocationBranchId)
                LabelValue(stringResource(Res.string.label_booking_date), gr.bookingDate)
                LabelValue(stringResource(Res.string.label_delivery_date), gr.deliveryDate)
                LabelValue(stringResource(Res.string.label_delivery_type), gr.deliveryTypeId)
                LabelValue(stringResource(Res.string.label_private_mark), gr.privateMark)
                LabelValue(stringResource(Res.string.label_remarks), gr.remarks)
                LabelValue(stringResource(Res.string.label_pod_required), gr.isPodRequired)
                LabelValue(stringResource(Res.string.label_pod_status), gr.podStatusId)
                LabelValue(stringResource(Res.string.label_created_by), gr.createdBy)
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
                text = if (expanded) stringResource(Res.string.label_see_less) else stringResource(Res.string.label_see_more),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) stringResource(Res.string.cd_expand_less) else stringResource(Res.string.cd_expand_more)
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