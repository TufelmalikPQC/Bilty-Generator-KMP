package com.bilty.generator.uiToolKit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData

/**
 * A common WebView composable that displays HTML content.
 * This works across Android, iOS, Desktop (JVM), and Web (Wasm)
 * thanks to the compose-webview-multiplatform library.
 */
@Composable
fun HtmlView(html: String, modifier: Modifier = Modifier) {
    // The library provides a state holder that can load HTML data directly.
    // It automatically handles updates when the `html` string changes.
    val webViewState = rememberWebViewStateWithHTMLData(data = html)

    // Use the WebView composable from the library.
    WebView(
        state = webViewState,
        modifier = modifier
    )
}