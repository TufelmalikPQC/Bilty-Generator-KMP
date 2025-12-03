package com.bilty.generator.bridge

import com.bilty.generator.model.constants.FirebaseConstants
import com.bilty.generator.utils.returnFirebaseOptions
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import platform.Foundation.NSLog

actual fun initializeFirebase() {
    try {
        // Initialize Firebase for iOS with configuration from google-services.json
        Firebase.initialize(options = returnFirebaseOptions())
        NSLog("FirebaseInitializer: Firebase initialized successfully for iOS")
    } catch (e: Exception) {
        NSLog("FirebaseInitializer: Failed to initialize Firebase on iOS - ${e.message}")
    }
}