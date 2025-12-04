package com.bilty.generator.di.helperModules

import com.bilty.generator.bridge.PrinterManager
import com.bilty.generator.utils.helpers.FirebaseRemotePrintHelper
import com.bilty.generator.utils.helpers.FirebasePrintQueueHelper
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Koin module for Helper classes and utilities.
 * 
 * Helper classes are singleton-scoped to ensure consistent state
 * and efficient resource usage across the application.
 */
val helperModule = module {
    
    /**
     * FirebaseRemotePrintHelper - Manages Firebase Realtime Database operations
     * for remote printing functionality
     * Scope: Singleton - shared instance for all print operations
     */
    singleOf(::FirebaseRemotePrintHelper)
    
    /**
     * FirebasePrintQueueHelper - Manages Firebase operations for print queue
     * with dual-node structure (printJobs + printIndex)
     * Scope: Singleton - shared instance for all queue operations
     */
    singleOf(::FirebasePrintQueueHelper)
}
