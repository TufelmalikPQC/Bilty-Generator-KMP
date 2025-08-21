package com.bilty.generator.uiToolKit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData

/**
 * A common WebView composable that displays HTML content and is zoomable.
 * This works across Android, iOS, Desktop (JVM), and Web (Wasm).
 */
@Composable
fun HtmlView(html: String, modifier: Modifier = Modifier) {
    val webViewState = rememberWebViewStateWithHTMLData(data = html)

    WebView(
        state = webViewState,
        modifier = modifier
    )
}