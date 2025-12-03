package com.bilty.generator

import androidx.compose.ui.window.ComposeUIViewController
import com.bilty.generator.bridge.initializeFirebase

fun MainViewController() = ComposeUIViewController {
    // Initialize Firebase before creating UI (only runs once per controller instance)
    initializeFirebase()
    KMPApp()
}