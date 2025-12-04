package com.bilty.generator.modules.remotePrint.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import biltygenerator.composeapp.generated.resources.Res
import biltygenerator.composeapp.generated.resources.label_active_prefix
import biltygenerator.composeapp.generated.resources.label_branch_id
import biltygenerator.composeapp.generated.resources.label_company_id
import biltygenerator.composeapp.generated.resources.label_manager_id
import biltygenerator.composeapp.generated.resources.label_registration_date
import com.bilty.generator.model.data.Branch
import com.bilty.generator.theme.ThemeColors
import org.jetbrains.compose.resources.stringResource

@Composable
fun BranchItem(
    branch: Branch,
    isSelected: Boolean = false,
    onSelected: () -> Unit = {}
) {
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

        // ===== TOP: Name + Code =====
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = branch.branchName.orEmpty(),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = branch.branchCode.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // ===== DETAILS WITH LABELS =====
        LabelValue(stringResource(Res.string.label_branch_id), "#" + branch.id)
        LabelValue(stringResource(Res.string.label_company_id), branch.companyId)
        LabelValue(stringResource(Res.string.label_manager_id), branch.branchManagerId)
        LabelValue(stringResource(Res.string.label_registration_date), branch.registrationDate)

        // Only one that always has a label
        Text(
            text = stringResource(Res.string.label_active_prefix, branch.isActive.toString()),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
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
