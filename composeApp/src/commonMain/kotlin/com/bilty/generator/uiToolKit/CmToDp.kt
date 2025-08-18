package com.bilty.generator.uiToolKit

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun cmToDp(cm: Float, density: Density): Dp {
    return (cm * 63f).dp // approx conversion
}
