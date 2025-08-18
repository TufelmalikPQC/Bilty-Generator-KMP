package com.bilty.generator.model.interfaces

import com.bilty.generator.model.data.RoadLineDeliveryReceipt

interface PdfGenerator {
    suspend fun generatePdf(
        receipt: RoadLineDeliveryReceipt,
        isPreviewWithImageBitmap: Boolean
    ): Boolean
}