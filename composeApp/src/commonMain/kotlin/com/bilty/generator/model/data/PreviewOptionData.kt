package com.bilty.generator.model.data

import androidx.compose.ui.graphics.vector.ImageVector

data class PreviewOptionData(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)