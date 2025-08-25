package com.bilty.generator.model.constants

import biltygenerator.composeapp.generated.resources.Res
import biltygenerator.composeapp.generated.resources.pqc_logo

object Constants {
    const val RECEIPT_IMAGE_PATH = "transport_road_line_invoice.jpeg"
    val DESKTOP_ICON = Res.drawable.pqc_logo

    object Platforms {
        const val PLATFORM_ANDROID = "Android"
        const val PLATFORM_IOS = "iOS"
        const val PLATFORM_DESKTOP = "Java"
        const val PLATFORM_WEB = "Web with Kotlin/Wasm"
    }
}