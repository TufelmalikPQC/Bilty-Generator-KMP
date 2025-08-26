package com.bilty.generator.uiToolKit

import com.bilty.generator.model.constants.Constants.Fonts

fun getFontFamilyName(fontFamilyName: String): String {
    return when (fontFamilyName) {
        "Dot Matrix" -> "font/${Fonts.DOT_MATRIX_7}"
        "Enhanced Dot Matrix" -> "font/${Fonts.ENHANCED_DOT_DIGITAL_7}"
        "Digi Trace" -> "font/${Fonts.DIGI_TRACE}"
        "Dot Digital-7" -> "font/${Fonts.DOT_DIGITAL_7}"
        "Digital-7" -> "font/${Fonts.DIGITAL_7}"
        else -> "font/${Fonts.DOT_MATRIX_7}"
    }
}