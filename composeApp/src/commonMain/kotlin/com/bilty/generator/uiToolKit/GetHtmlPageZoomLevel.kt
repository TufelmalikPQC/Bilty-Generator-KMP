package com.bilty.generator.uiToolKit

import com.bilty.generator.getPlatform
import com.bilty.generator.model.constants.Constants.Platforms.PLATFORM_ANDROID
import com.bilty.generator.model.constants.Constants.Platforms.PLATFORM_DESKTOP
import com.bilty.generator.model.constants.Constants.Platforms.PLATFORM_IOS
import com.bilty.generator.model.constants.Constants.Platforms.PLATFORM_WEB

fun getHtmlPageZoomLevel(): Double {
    return when {
        getPlatform().name.contains(PLATFORM_ANDROID) -> 0.52
        getPlatform().name.contains(PLATFORM_IOS) -> 0.70
        getPlatform().name.contains(PLATFORM_DESKTOP) -> 0.80
        getPlatform().name.contains(PLATFORM_WEB) -> 0.80
        else -> 0.85
    }
}