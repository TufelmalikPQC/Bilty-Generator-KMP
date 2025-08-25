package com.bilty.generator.bridge


import kotlinx.browser.window

actual fun openUrlInBrowser(url: String) {
    window.open(url, "_blank")
}