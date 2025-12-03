package com.bilty.generator.bridge

import android.util.Log

actual fun initializeFirebase() {
    try {
        // Firebase is automatically initialized on Android via google-services.json
        // The GoogleServices plugin handles the initialization
        Log.d("FirebaseInitializer", "Firebase initialized successfully for Android")
    } catch (e: Exception) {
        Log.e("FirebaseInitializer", "Failed to initialize Firebase on Android", e)
    }
}