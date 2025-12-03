package com.bilty.generator.utils

import com.bilty.generator.model.constants.FirebaseConstants
import dev.gitlive.firebase.FirebaseOptions

fun returnFirebaseOptions(): FirebaseOptions {
    return FirebaseOptions(
        applicationId = FirebaseConstants.APPLICATION_ID,
        apiKey = FirebaseConstants.API_KEY,
        databaseUrl = FirebaseConstants.REALTIME_DATABASE_URL,
        projectId = FirebaseConstants.PROJECT_ID,
        gcmSenderId = FirebaseConstants.GCM_SENDER_ID
    )
}