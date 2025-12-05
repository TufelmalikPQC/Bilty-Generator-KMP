package com.bilty.generator.di.repositoryModules

import com.bilty.generator.repository.SendPrintRequestRepository
import com.bilty.generator.repository.PrintQueueRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Koin module for Repositories.
 * 
 * Repositories are singleton-scoped, meaning a single instance is shared
 * across the entire application lifecycle.
 */
val repositoryModule = module {
    
    /**
     * SendPrintRequestRepository - Handles print request data operations
     * Dependencies: FirebaseRemotePrintHelper (injected via constructor if needed)
     * Scope: Singleton - single instance throughout the app
     */
    singleOf(::SendPrintRequestRepository)
    
    /**
     * PrintQueueRepository - Manages print queue with dual-node Firebase structure
     * Dependencies: FirebasePrintQueueHelper (instantiated internally)
     * Scope: Singleton - single instance throughout the app
     */
    singleOf(::PrintQueueRepository)
}
