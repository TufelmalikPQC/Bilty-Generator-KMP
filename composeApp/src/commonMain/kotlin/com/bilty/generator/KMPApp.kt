package com.bilty.generator

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bilty.generator.model.enums.FontStyles
import com.bilty.generator.modules.AppRoutes
import com.bilty.generator.modules.preview.ui.PrintPreviewScreen
import com.bilty.generator.modules.print.ui.PrinterScreen
import com.bilty.generator.modules.printmethod.ui.PrintMethodSelectionScreen
import com.bilty.generator.modules.remotePrint.ui.SendPrintRequestScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import com.bilty.generator.di.appModule

@Composable
@Preview
fun KMPApp() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        val navController = rememberNavController()

        MaterialTheme {
            Scaffold { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = AppRoutes.SendPrintRequestScreen
                ) {
                    composable<AppRoutes.PrintMethodSelectionScreen> {
                        PrintMethodSelectionScreen(navController)
                    }

                    composable<AppRoutes.PrintPreviewScreen> {
                        PrintPreviewScreen(navController)
                    }

                    composable<AppRoutes.PrinterScreen> { route ->
                        route.savedStateHandle.apply {
                            val isPreviewWithImageBitmap =
                                get<Boolean>("isPreviewWithImageBitmap") ?: false
                            val fontSize = get<Int>("fontSize") ?: 24
                            val isLandscapeMode = get<Boolean>("isLandscapeMode") ?: false
                            val fontFamilyName =
                                get<String>("fontFamilyName") ?: FontStyles.DOT_MATRIX.name

                            PrinterScreen(
                                navController = navController,
                                isPreviewWithImageBitmap = isPreviewWithImageBitmap,
                                fontSize = fontSize,
                                isLandscapeMode = isLandscapeMode,
                                fontFamilyName = fontFamilyName
                            )
                        }
                    }


                    composable<AppRoutes.SendPrintRequestScreen> {
                        SendPrintRequestScreen(paddingValues)
                    }
                }
            }
        }
    }

}