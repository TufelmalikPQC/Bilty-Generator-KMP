package com.bilty.generator.model.data

import androidx.compose.ui.graphics.Color

data class StatusConfig(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconColor: Color,
    val backgroundColor: Color,
    val label: String
)