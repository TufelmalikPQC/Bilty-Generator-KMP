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

// Add this helper function at the top level of the file to convert NSData
@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray = ByteArray(this.length.toInt()).apply {
    this@toByteArray.getBytes(this.refTo(0), this@toByteArray.length)
}

class PdfGeneratorIos : PdfGenerator {
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun generatePdf(
        receipt: RoadLineDeliveryReceipt,
        isPreviewWithImageBitmap: Boolean
    ): ByteArray? { // <-- Return ByteArray?
        return try {
            val html = generateRoadLineDeliveryReceipt(receipt, isPreviewWithImageBitmap)
            // ... (keep the existing printFormatter and renderer setup) ...

            val pdfData = NSMutableData() // You already have this
            UIGraphicsBeginPDFContextToData(pdfData, paperRect, null)

            // ... (keep the existing page rendering loop) ...

            UIGraphicsEndPDFContext()

            // 1. Instead of saving, just convert the NSData to ByteArray and return
            pdfData.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}