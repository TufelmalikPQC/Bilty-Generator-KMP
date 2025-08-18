package com.bilty.generator.bridge

import com.bilty.generator.uiToolKit.generateRoadLineDeliveryReceipt
import com.bilty.generator.model.data.RoadLineDeliveryReceipt
import com.bilty.generator.model.interfaces.PdfGenerator
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSMutableData
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSValue
import platform.Foundation.setValue
import platform.Foundation.writeToFile
import platform.UIKit.UIGraphicsBeginPDFContextToData
import platform.UIKit.UIGraphicsBeginPDFPage
import platform.UIKit.UIGraphicsEndPDFContext
import platform.UIKit.UIGraphicsGetPDFContextBounds
import platform.UIKit.UIMarkupTextPrintFormatter
import platform.UIKit.UIPrintPageRenderer
import platform.UIKit.valueWithCGRect


actual fun getPdfGenerator(): PdfGenerator = PdfGeneratorIos()

class PdfGeneratorIos : PdfGenerator {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun generatePdf(receipt: RoadLineDeliveryReceipt, isPreviewWithImageBitmap:  Boolean): Boolean {
        return try {

            val html = generateRoadLineDeliveryReceipt(receipt,isPreviewWithImageBitmap)
            val printFormatter = UIMarkupTextPrintFormatter(html)

            val renderer = UIPrintPageRenderer()
            renderer.addPrintFormatter(printFormatter, startingAtPageAtIndex = 0L)

            val paperRect = CGRectMake(0.0, 0.0, 600.0, 400.0)
            val printableRect = CGRectMake(0.0, 0.0, 600.0, 400.0)

            // Set paper and printable reacts
            renderer.setValue(NSValue.valueWithCGRect(paperRect), forKey = "paperRect")
            renderer.setValue(NSValue.valueWithCGRect(printableRect), forKey = "printableRect")

            val pdfData = NSMutableData()
            UIGraphicsBeginPDFContextToData(pdfData, paperRect, null)

            val pageCount = renderer.numberOfPages.toInt()
            println("📄 Rendering $pageCount pages")

            for (i in 0 until pageCount) {
                UIGraphicsBeginPDFPage()
                UIGraphicsGetPDFContextBounds().useContents {
                    renderer.drawPageAtIndex(
                        i.toLong(),
                        CGRectMake(origin.x, origin.y, size.width, size.height)
                    )
                }
            }

            UIGraphicsEndPDFContext()

            // Save to Documents directory (iOS doesn't have direct access to Downloads)
            val documentsDir = NSSearchPathForDirectoriesInDomains(
                NSDocumentDirectory,
                NSUserDomainMask,
                true
            ).firstOrNull() as? String

            if (documentsDir == null) {
                println("❌ Could not access Documents directory")
                return false
            }

            val filePath = "$documentsDir/receipt-${receipt.receiptNumber}.pdf"
            val success = pdfData.writeToFile(filePath, atomically = true)

            if (success) {
                println("✅ PDF saved successfully at: $filePath")

                // Try to save to Files app if available
                try {
                    val fileManager = NSFileManager.defaultManager
                    val downloadsPath = "$documentsDir/Downloads"

                    // Create Downloads folder if it doesn't exist
                    if (!fileManager.fileExistsAtPath(downloadsPath)) {
                        fileManager.createDirectoryAtPath(
                            downloadsPath,
                            withIntermediateDirectories = true,
                            attributes = null,
                            error = null
                        )
                    }

                    val downloadFilePath = "$downloadsPath/receipt-${receipt.receiptNumber}.pdf"
                    if (fileManager.copyItemAtPath(
                            filePath,
                            toPath = downloadFilePath,
                            error = null
                        )
                    ) {
                        println("✅ PDF also saved to Downloads folder: $downloadFilePath")
                    }
                } catch (e: Exception) {
                    println("⚠️ Could not copy to Downloads folder: ${e.message}")
                }

                return true
            } else {
                println("❌ Failed to save PDF to file")
                return false
            }

        } catch (e: Exception) {
            println("❌ Unexpected error in iOS PDF generation: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}