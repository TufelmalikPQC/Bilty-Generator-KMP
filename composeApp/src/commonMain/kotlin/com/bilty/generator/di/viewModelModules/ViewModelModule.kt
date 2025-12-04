package com.bilty.generator.di.viewModelModules

import com.bilty.generator.modules.print.ui.PrinterViewModel
import com.bilty.generator.modules.remotePrint.ui.SendPrintRequestViewModel
import com.bilty.generator.modules.printqueue.PrintQueueViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module for ViewModels.
 * 
 * This module defines how ViewModels should be instantiated and managed by Koin.
 * ViewModels are scoped to the Composable lifecycle using the viewModel scope.
 */
val viewModelModule = module {
    
    /**
     * PrinterViewModel - Manages printer selection and printing operations
     * Dependencies: PrinterManager (injected via constructor)
     */
    viewModelOf(::PrinterViewModel)
    
    /**
     * SendPrintRequestViewModel - Manages remote print request operations
     * Dependencies: SendPrintRequestRepository (injected via constructor)
     */
    viewModelOf(::SendPrintRequestViewModel)
    
    /**
     * PrintQueueViewModel - Manages print queue operations
     * Dependencies: PrintQueueRepository (injected via constructor)
     */
    viewModelOf(::PrintQueueViewModel)
}
