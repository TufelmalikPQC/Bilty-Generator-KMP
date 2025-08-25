package com.bilty.generator.bridge


import java.awt.Desktop
import java.io.File

actual fun openUrlInBrowser(url: String) {
    if (Desktop.isDesktopSupported()) {
        openHtmlFromUri(url)
    }
}
fun openHtmlFromUri(htmlContent: String) {
    val tempFile = File.createTempFile("receipt_", ".html")
    tempFile.writeText(htmlContent)

    val uri = tempFile.toURI() // This is a valid file URI
    Desktop.getDesktop().browse(uri)
}


/*
fun openHtmlFileInBrowser(htmlContent: String) {
    val tempFile = File.createTempFile("receipt_", ".html")
    tempFile.writeText(htmlContent)
    Desktop.getDesktop().browse(tempFile.toURI())
}*/
