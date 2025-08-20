package com.bilty.generator.modules

import kotlinx.serialization.Serializable

@Serializable
sealed class AppRoutes {
    @Serializable
    data object PrintPreviewScreen : AppRoutes()

    @Serializable
    data class PrinterScreen(var isPreviewWithImageBitmap: Boolean) : AppRoutes()
}