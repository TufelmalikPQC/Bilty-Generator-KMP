package com.bilty.generator.modules.remotePrint.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.graphics.Color
import biltygenerator.composeapp.generated.resources.Res
import biltygenerator.composeapp.generated.resources.status_cancelled
import biltygenerator.composeapp.generated.resources.status_completed
import biltygenerator.composeapp.generated.resources.status_failed
import biltygenerator.composeapp.generated.resources.status_not_started
import biltygenerator.composeapp.generated.resources.status_not_supported
import biltygenerator.composeapp.generated.resources.status_pending
import biltygenerator.composeapp.generated.resources.status_printing
import com.bilty.generator.model.data.StatusConfig
import com.bilty.generator.model.enums.PrintStatus
import com.bilty.generator.theme.ThemeColors
import org.jetbrains.compose.resources.stringResource

import androidx.compose.runtime.Composable

@Composable
fun getStatusConfig(status: PrintStatus): StatusConfig {
    return when (status) {
        PrintStatus.PENDING -> StatusConfig(
            icon = Icons.Filled.Schedule,
            iconColor = ThemeColors.statusOrange,
            backgroundColor = ThemeColors.statusBackgroundLightOrange,
            label = stringResource(Res.string.status_pending)
        )

        PrintStatus.PRINTING -> StatusConfig(
            icon = Icons.Filled.Print,
            iconColor = ThemeColors.statusBlue,
            backgroundColor = ThemeColors.statusBackgroundLightBlue,
            label = stringResource(Res.string.status_printing)
        )

        PrintStatus.COMPLETED -> StatusConfig(
            icon = Icons.Filled.CheckCircle,
            iconColor = ThemeColors.statusGreen,
            backgroundColor = ThemeColors.statusBackgroundLightGreen,
            label = stringResource(Res.string.status_completed)
        )

        PrintStatus.FAILED -> StatusConfig(
            icon = Icons.Filled.Error,
            iconColor = ThemeColors.statusRed,
            backgroundColor = ThemeColors.statusBackgroundLightRed,
            label = stringResource(Res.string.status_failed)
        )

        PrintStatus.CANCELLED -> StatusConfig(
            icon = Icons.Filled.Cancel,
            iconColor = ThemeColors.statusDarkOrange,
            backgroundColor = ThemeColors.statusBackgroundLightOrangeRed,
            label = stringResource(Res.string.status_cancelled)
        )
    }
}