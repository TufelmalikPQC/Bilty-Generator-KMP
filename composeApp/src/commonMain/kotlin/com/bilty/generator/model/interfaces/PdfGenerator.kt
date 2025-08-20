package com.bilty.generator.model.interfaces

import com.bilty.generator.model.data.RoadLineDeliveryReceipt

interface PdfGenerator {
    /**
     * Generates a PDF and returns its content as a ByteArray.
     * @return The PDF data as a ByteArray on success, or null on failure.
     */
    suspend fun generatePdf(
        receipt: RoadLineDeliveryReceipt,
        isPreviewWithImageBitmap: Boolean,
        zoomLevel: Double
    ): ByteArray?
}