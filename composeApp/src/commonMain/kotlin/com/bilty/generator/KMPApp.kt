package com.bilty.generator

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bilty.generator.modules.AppRoutes
import com.bilty.generator.modules.preview.PrintPreviewScreen
import com.bilty.generator.modules.print.PrinterScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun KMPApp() {
    val navController = rememberNavController()

    MaterialTheme {
        NavHost(
            navController = navController,
            startDestination = AppRoutes.PrintPreviewScreen
        ) {
            composable<AppRoutes.PrintPreviewScreen> {
                PrintPreviewScreen(navController)
            }

            composable<AppRoutes.PrinterScreen> { route ->
                route.savedStateHandle.apply {
                    val isPreviewWithImageBitmap = get<Boolean>("isPreviewWithImageBitmap") ?: false
                    val fontSize = get<Int>("fontSize") ?: 24
                    val isLandscapeMode = get<Boolean>("isLandscapeMode") ?: false
                    val fontFamilyName = get<String>("fontFamilyName") ?: "Dot Matrix"

                    PrinterScreen(
                        navController = navController,
                        isPreviewWithImageBitmap = isPreviewWithImageBitmap,
                        fontSize = fontSize,
                        isLandscapeMode = isLandscapeMode,
                        fontFamilyName = fontFamilyName
                    )
                }
            }
        }
    }

}