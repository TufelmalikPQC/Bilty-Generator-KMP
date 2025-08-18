package com.bilty.generator.bridge


import com.bilty.generator.model.data.RoadLineDeliveryReceipt
import com.bilty.generator.model.interfaces.PdfGenerator
import com.bilty.generator.uiToolKit.generateRoadLineDeliveryReceipt
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement

external interface Html2Pdf {
    fun from(element: org.w3c.dom.Element): Html2Pdf
    fun save(fileName: String)
}

external fun html2pdf(): Html2Pdf


class PdfGeneratorWeb : PdfGenerator {
    override suspend fun generatePdf(
        receipt: RoadLineDeliveryReceipt,
        isPreviewWithImageBitmap: Boolean
    ): Boolean {
        val html = generateRoadLineDeliveryReceipt(receipt, isPreviewWithImageBitmap)
        val div = document.createElement("div") as HTMLDivElement
        div.innerHTML = html
        document.body?.appendChild(div)

        html2pdf().from(div).save("receipt-${receipt.receiptNumber}.pdf")

        div.remove()
        return true
    }
}

actual fun getPdfGenerator(): PdfGenerator = PdfGeneratorWeb()