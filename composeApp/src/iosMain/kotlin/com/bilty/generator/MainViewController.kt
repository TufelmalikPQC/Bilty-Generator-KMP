package com.bilty.generator

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    // Initialize Firebase before creating UI (only runs once per controller instance)
    KMPApp()
}