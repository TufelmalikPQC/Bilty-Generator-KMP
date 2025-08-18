package com.bilty.generator.modules

import kotlinx.serialization.Serializable

@Serializable
sealed class AppRoutes {
    @Serializable
    data object PrinterScreen : AppRoutes()
    @Serializable
    data object PrintPreviewScreen : AppRoutes()
}