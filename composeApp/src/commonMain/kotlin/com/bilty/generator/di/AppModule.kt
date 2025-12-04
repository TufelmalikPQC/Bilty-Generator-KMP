package com.bilty.generator.di

import com.bilty.generator.di.helperModules.helperModule
import com.bilty.generator.di.repositoryModules.repositoryModule
import com.bilty.generator.di.viewModelModules.viewModelModule
import org.koin.dsl.module

/**
 * Main Koin module that consolidates all dependency injection modules.
 * 
 * This module aggregates all sub-modules (ViewModels, Repositories, Helpers)
 * into a single module for easy application initialization.
 * 
 */
val appModule = module {
    includes(
        viewModelModule,
        repositoryModule,
        helperModule
    )
}
