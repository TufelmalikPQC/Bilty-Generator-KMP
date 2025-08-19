package com.bilty.generator.bridge

import com.bilty.generator.model.data.PrinterInfo
import com.bilty.generator.model.enums.PrintStatus
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIPrintInfo
import platform.UIKit.UIPrintInfoOutputTypeGeneral
import platform.UIKit.UIPrintInteractionController
import platform.UIKit.UIViewController
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// Helper to convert ByteArray
@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData = NSData.create(bytes = this, length = this.size.toULong())

// Helper to get the top UIViewController
object ViewControllerProvider {
    var topViewController: UIViewController? = null
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PrinterManager {
    actual fun getAvailablePrinters(): List<PrinterInfo> {
        // iOS does not provide an API to list AirPrint printers programmatically.
        return emptyList()
    }

    actual suspend fun printPdf(data: ByteArray, printerName: String?): PrintStatus {
        val printController = UIPrintInteractionController.sharedPrintController
        if (printController == null || !UIPrintInteractionController.isPrintingAvailable()) {
            return PrintStatus.FAILED
        }

        val viewController = ViewControllerProvider.topViewController ?: return PrintStatus.FAILED

        return suspendCoroutine { continuation ->
            printController.printInfo = UIPrintInfo.printInfo().apply {
                outputType = UIPrintInfoOutputTypeGeneral
                jobName = "Your_App_Document"
            }
            printController.printingItem = data.toNSData()

            printController.presentAnimated(true) { _, completed, error ->
                when {
                    error != null -> continuation.resume(PrintStatus.FAILED)
                    completed -> continuation.resume(PrintStatus.SUCCESS) // User confirmed the print dialog
                    else -> continuation.resume(PrintStatus.CANCELLED)
                }
            }
        }
    }

    actual suspend fun printRawText(ipAddress: String, port: Int, text: String): PrintStatus {

    }
}