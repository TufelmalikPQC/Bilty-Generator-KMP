package com.bilty.generator

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.bilty.generator.model.constants.Constants.DESKTOP_ICON
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Bilty Generator ",
        icon = painterResource(DESKTOP_ICON)
    ) {
        KMPApp()
    }
}