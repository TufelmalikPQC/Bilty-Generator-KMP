package com.bilty.generator.bridge


import com.bilty.generator.model.constants.FirebaseConstants
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize

actual fun initializeFirebase() {
    try {
        println("Initializing GitLive Firebase for JVM...")
        // Initialize GitLive Firebase (no native SDK needed)
        Firebase.initialize(
            context = null,
            options = FirebaseOptions(
                applicationId = FirebaseConstants.APPLICATION_ID,
                apiKey = FirebaseConstants.API_KEY,
                databaseUrl = FirebaseConstants.REALTIME_DATABASE_URL,
                projectId = FirebaseConstants.PROJECT_ID,
            )
        )
        println("✓ GitLive Firebase initialized successfully")

    } catch (e: Exception) {
        println("Firebase initialization failed: ${e.message}")
        e.printStackTrace()
        throw e
    }
}