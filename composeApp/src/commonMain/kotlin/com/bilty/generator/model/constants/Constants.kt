package com.bilty.generator.model.constants

import biltygenerator.composeapp.generated.resources.Res
import biltygenerator.composeapp.generated.resources.pqc_logo

object Constants {
    const val RECEIPT_IMAGE_PATH = "transport_road_line_invoice.jpeg"

    object Fonts {
        const val BASE_PREFIX = "font/"
        const val FONT_FAMILY_NAME = "DynamicFontFamily"
        const val DIGI_TRACE = "digi_trace.otf"
        const val ENHANCED_DOT_DIGITAL_7 = "enhanced_dot_digital_7.ttf"
        const val DOT_DIGITAL_7  = "dot_matrix.ttf"
        const val DOT_MATRIX_7 = "dot_digital_7.ttf"
        const val DIGITAL_7 = "digital_7.ttf"
    }

    val DESKTOP_ICON = Res.drawable.pqc_logo

    object Platforms {
        const val PLATFORM_ANDROID = "Android"
        const val PLATFORM_IOS = "iOS"
        const val PLATFORM_DESKTOP = "Java"
        const val PLATFORM_WEB = "Web with Kotlin/Wasm"
    }


    // Receipt size: 148 mm × 105 mm
    const val receiptWidthInches = 5.82
    const val receiptHeightInches = 4.12

    const val RECEIPT_WIDTH_POINTS = receiptWidthInches * 72
    const val RECEIPT_HEIGHT_POINTS = receiptHeightInches * 72
}