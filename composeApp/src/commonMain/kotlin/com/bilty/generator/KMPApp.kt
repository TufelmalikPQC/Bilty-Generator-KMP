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
                route.savedStateHandle.get<Boolean>("isPreviewWithImageBitmap")
                    ?.let {
                        PrinterScreen(
                            navController = navController,
                            isPreviewWithImageBitmap = it
                        )
                    }
            }
        }
    }

}