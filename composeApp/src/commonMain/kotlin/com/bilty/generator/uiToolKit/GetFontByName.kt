package com.bilty.generator.uiToolKit

import com.bilty.generator.model.constants.Constants.Fonts
import com.bilty.generator.model.constants.Constants.Fonts.BASE_PREFIX
import com.bilty.generator.model.enums.FontStyles

fun getFontFamilyName(fontFamilyName: FontStyles): String {
    return when (fontFamilyName) {
        FontStyles.DOT_MATRIX -> "$BASE_PREFIX${Fonts.DOT_MATRIX_7}"
        FontStyles.ENHANCED_DOT_MATRIX -> "$BASE_PREFIX${Fonts.ENHANCED_DOT_DIGITAL_7}"
        FontStyles.DIGI_TRACE -> "$BASE_PREFIX${Fonts.DIGI_TRACE}"
        FontStyles.DOT_DIGITAL_7 -> "$BASE_PREFIX${Fonts.DOT_DIGITAL_7}"
        FontStyles.DIGITAL_7 -> "$BASE_PREFIX${Fonts.DIGITAL_7}"
        else -> "$BASE_PREFIX${Fonts.DOT_MATRIX_7}"
    }
}