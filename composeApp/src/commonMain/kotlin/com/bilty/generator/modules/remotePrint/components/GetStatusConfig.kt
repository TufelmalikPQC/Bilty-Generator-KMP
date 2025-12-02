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
import com.bilty.generator.model.data.StatusConfig
import com.bilty.generator.model.enums.PrintStatus

fun getStatusConfig(status: PrintStatus): StatusConfig {
    return when (status) {
        PrintStatus.NOT_STARTED -> StatusConfig(
            icon = Icons.Filled.FiberManualRecord,
            iconColor = Color(0xFF757575), // Gray
            backgroundColor = Color(0xFFF5F5F5), // Light Gray
            label = "Not Started"
        )
        PrintStatus.PENDING -> StatusConfig(
            icon = Icons.Filled.Schedule,
            iconColor = Color(0xFFF57C00), // Orange
            backgroundColor = Color(0xFFFFF3E0), // Light Orange
            label = "Pending"
        )
        PrintStatus.PRINTING -> StatusConfig(
            icon = Icons.Filled.Print,
            iconColor = Color(0xFF1976D2), // Blue
            backgroundColor = Color(0xFFE3F2FD), // Light Blue
            label = "Printing"
        )
        PrintStatus.COMPLETED -> StatusConfig(
            icon = Icons.Filled.CheckCircle,
            iconColor = Color(0xFF388E3C), // Green
            backgroundColor = Color(0xFFE8F5E9), // Light Green
            label = "Completed"
        )
        PrintStatus.FAILED -> StatusConfig(
            icon = Icons.Filled.Error,
            iconColor = Color(0xFFD32F2F), // Red
            backgroundColor = Color(0xFFFFEBEE), // Light Red
            label = "Failed"
        )
        PrintStatus.CANCELLED -> StatusConfig(
            icon = Icons.Filled.Cancel,
            iconColor = Color(0xFFE65100), // Dark Orange
            backgroundColor = Color(0xFFFBE9E7), // Light Orange/Red
            label = "Cancelled"
        )
        PrintStatus.NOT_SUPPORTED -> StatusConfig(
            icon = Icons.Filled.Block,
            iconColor = Color(0xFF6A1B9A), // Purple
            backgroundColor = Color(0xFFF3E5F5), // Light Purple
            label = "Not Supported"
        )
    }
}