package com.bilty.generator.uiToolKit

import com.bilty.generator.model.constants.Constants.RECEIPT_IMAGE_PATH
import com.bilty.generator.model.data.BiltyChargesTable
import com.bilty.generator.model.data.RoadLineDeliveryReceipt

suspend fun generateRoadLineDeliveryReceipt(
    receipt: RoadLineDeliveryReceipt,
    isPreviewWithImageBitmap: Boolean,
    isForPreview: Boolean,
    zoomLevel: Double = getHtmlPageZoomLevel()
): String {

    val receiptImageBase64 = getImageAsBase64Code(RECEIPT_IMAGE_PATH)

    val backgroundImage = if (isPreviewWithImageBitmap && receiptImageBase64.isNotEmpty()) {
        "background-image: url('data:image/jpeg;base64,$receiptImageBase64');"
    } else {
        "background-color: transparent;"
    }


    return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8"/>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=yes"/>
            <title>Lunia Roadlines - Delivery Receipt</title>
            <style>
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                }

                 html, body {
                    width: 100%;
                    height: 100%;
                    margin: 0;
                    padding: 0;
                    ${if (isForPreview) "zoom: $zoomLevel; transform: scale($zoomLevel);" else ""}
                    font-family: Arial, sans-serif;
                    overflow: auto;
                }

                @page {
                    size: landscape;
                    margin: 0;
                }

                .form-container {
                    position: relative;
                    width: 100%;
                    height: 100%;
                    margin: 0 auto;
                    $backgroundImage
                    background-size: 100% 100%;
                    background-repeat: no-repeat;
                    background-position: center;
                    page-break-inside: avoid;
                }

                .form-field {
                    position: absolute;
                    border: none;
                    background: transparent;
                    font-size: 24px;
                    padding: 2px;
                    outline: none;
                }

                textarea.form-field {
                    font-family: Arial, sans-serif;
                }

                @media print {
                    body {
                        -webkit-print-color-adjust: exact;
                        print-color-adjust: exact;
                    }
                }
            </style>
        </head>
        <body>
            <div class="form-container">
                <div class="form-field" style="left: 13.01%; top: 18.66%;">${receipt.receiptNumber}</div>
                <div class="form-field" style="left: 36.53%; top: 18.66%;">${receipt.branchName}</div>
                <div class="form-field" style="left: 69.70%; top: 18.17%;">${receipt.date}</div>

                <div class="form-field" style="left: 20.57%; top: 26.59%;">${receipt.consignee}</div>
                <div class="form-field" style="left: 20.57%; top: 35.00%;">${receipt.consignor}</div>
                <div class="form-field" style="left: 20.57%; top: 43.29%;">${receipt.biltyNumber}</div>
                <div class="form-field" style="left: 20.57%; top: 51.59%;">${receipt.biltyDate}</div>
                <div class="form-field" style="left: 20.57%; top: 59.76%;">${receipt.fromLocation}</div>
                <div class="form-field" style="left: 20.57%; top: 68.29%;">${receipt.packageCount}</div>
                <div class="form-field" style="left: 20.57%; top: 76.22%;">${receipt.particulars}</div>
                <div class="form-field" style="left: 20.57%; top: 84.51%;">${receipt.pMarka}</div>

                <div class="form-field" style="left: 77.27%; top: 25.98%; width: 140px; text-align: right;">${receipt.biltyChargesTable.freight}</div>
                <div class="form-field" style="left: 77.27%; top: 34.76%; width: 140px; text-align: right;">${receipt.biltyChargesTable.charity}</div>
                <div class="form-field" style="left: 77.27%; top: 43.29%; width: 140px; text-align: right;">${receipt.biltyChargesTable.handling}</div>
                <div class="form-field" style="left: 77.27%; top: 51.34%; width: 140px; text-align: right;">${receipt.biltyChargesTable.delivery}</div>
                <div class="form-field" style="left: 77.27%; top: 59.76%; width: 140px; text-align: right;">${receipt.biltyChargesTable.ddCharges}</div>
                <div class="form-field" style="left: 77.27%; top: 68.05%; width: 140px; text-align: right;">${receipt.biltyChargesTable.demurrage}</div>
                <div class="form-field" style="left: 77.27%; top: 75.98%; width: 140px; text-align: right;">${receipt.biltyChargesTable.otherCharges}</div>
                <div class="form-field" style="left: 77.27%; top: 80.98%; width: 140px; text-align: right; font-weight: bold; font-size: 1.625em;">${receipt.biltyChargesTable.grandTotal}</div>
            </div>
        </body>
        </html>
    """.trimIndent()
}

fun getDemoRoadLineDeliveryReceipt(): RoadLineDeliveryReceipt {
    return RoadLineDeliveryReceipt(
        receiptNumber = "12345678",
        branchName = "Central Warehouse",
        date = "14/08/2025",
        consignee = "XYZ Traders Pvt. Ltd., Mumbai",
        consignor = "ABC Industries Ltd., Delhi",
        biltyNumber = "BILTY-7890",
        biltyDate = "13/08/2025",
        fromLocation = "New Delhi",
        packageCount = 10,
        particulars = "Electronic Components",
        pMarka = "PM-2025-AUG",
        panNumber = "BGKPL8812J",
        biltyChargesTable = BiltyChargesTable(
            freight = 1500.0,
            charity = 50.0,
            handling = 200.0,
            delivery = 100.0,
            ddCharges = 30.0,
            demurrage = 0.0,
            otherCharges = 20.0,
            grandTotal = 1900.0
        )
    )
}