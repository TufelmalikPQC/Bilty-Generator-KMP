package com.bilty.generator.uiToolKit

import com.bilty.generator.model.constants.Constants.Fonts
import com.bilty.generator.model.constants.Constants.Fonts.BASE_PREFIX

fun getFontFamilyName(fontFamilyName: String): String {
    return when (fontFamilyName) {
        "Dot Matrix" -> "$BASE_PREFIX${Fonts.DOT_MATRIX_7}"
        "Enhanced Dot Matrix" -> "$BASE_PREFIX${Fonts.ENHANCED_DOT_DIGITAL_7}"
        "Digi Trace" -> "$BASE_PREFIX${Fonts.DIGI_TRACE}"
        "Dot Digital-7" -> "$BASE_PREFIX${Fonts.DOT_DIGITAL_7}"
        "Digital-7" -> "$BASE_PREFIX${Fonts.DIGITAL_7}"
        else -> "$BASE_PREFIX${Fonts.DOT_MATRIX_7}"
    }
}