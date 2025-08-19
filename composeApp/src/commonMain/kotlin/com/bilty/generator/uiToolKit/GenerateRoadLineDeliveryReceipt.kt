package com.bilty.generator.uiToolKit

import com.bilty.generator.bridge.getImageAsBase64
import com.bilty.generator.getPlatform
import com.bilty.generator.model.data.BiltyChargesTable
import com.bilty.generator.model.data.RoadLineDeliveryReceipt

fun generateRoadLineDeliveryReceipt(
    receipt: RoadLineDeliveryReceipt,
    isPreviewWithImageBitmap: Boolean
): String {

    var imagePath = "images/transport_roadline_invoice.jpeg"
    if(getPlatform().name.contains("Android")){
        imagePath  = "transport_roadline_invoice.jpeg"
    }
    val receiptImageBase64 = getImageAsBase64(imagePath)
    val zoomLevel = 0.80

    val backgroundImage = if (isPreviewWithImageBitmap) {
        "background-image: url('data:image/jpeg;base64,$receiptImageBase64');"
    } else {
        "background-color: #f0f0f0; border: 2px dashed red;"
    }

    // Helper to format currency values
    fun formatCurrency(value: Double) = String.format("%.2f", value)

    return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8"/>
            <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
            <title>Lunia Roadlines - Delivery Receipt</title>
            <style>
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                }

                /* Ensure the body fills the page and has no default margin or overflow */
                html, body {
                    width: 100%;
                    height: 100%;
                    margin: 0;
                    padding: 0;
                    font-family: Arial, sans-serif;
                    zoom: $zoomLevel;
                    transform: scale($zoomLevel)
                    overflow: hidden; /* Prevents scrollbars and content spillover */
                }

                /* This rule tells the browser to print the page in landscape mode and removes margins. */
                @page {
                    size: landscape;
                    margin: 0;
                }

                .form-container {
                    position: relative;
                    /* Dimensions adjusted to better match the provided receipt's aspect ratio */
                    width: 1191px;
                    height: 820px; /* Reduced height further to ensure bottom is not cut off */
                    margin: 0 auto;
                    $backgroundImage
                    background-size: 100% 100%; /* Stretch image to fit container */
                    background-repeat: no-repeat;
                    background-position: center;
                    page-break-inside: avoid; /* Prevents the element from being split across pages */
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

                /* Specific styles for printing */
                @media print {
                    body {
                        /* Ensures the background graphics (like your receipt image) are printed */
                        -webkit-print-color-adjust: exact; /* For Chrome, Safari, Edge */
                        print-color-adjust: exact; /* For Firefox */
                    }
                }
            </style>
        </head>
            <body>
                <div class="form-container">
                    <div class="form-field" style="left: 155px; top: 153px;">${receipt.receiptNumber}</div>
                    <div class="form-field" style="left: 435px; top: 153px;">${receipt.branchName}</div>
                    <div class="form-field" style="left: 830px; top: 149px;">${receipt.date}</div>

                    <div class="form-field" style="left: 245px; top: 218px;">${receipt.consignee}</div>
                    <div class="form-field" style="left: 245px; top: 287px;">${receipt.consignor}</div>
                    <div class="form-field" style="left: 245px; top: 355px;">${receipt.biltyNumber}</div>
                    <div class="form-field" style="left: 245px; top: 423px;">${receipt.biltyDate}</div>
                    <div class="form-field" style="left: 245px; top: 490px;">${receipt.fromLocation}</div>
                    <div class="form-field" style="left: 245px; top: 560px;">${receipt.packageCount}</div>
                    <div class="form-field" style="left: 245px; top: 625px;">${receipt.particulars}</div>
                    <div class="form-field" style="left: 245px; top: 693px;">${receipt.pMarka}</div>

                    <div class="form-field" style="left: 920px; top: 213px; width: 140px; text-align: right;">${
        formatCurrency(
            receipt.biltyChargesTable.freight
        )
    }</div>
                    <div class="form-field" style="left: 920px; top: 285px; width: 140px; text-align: right;">${
        formatCurrency(
            receipt.biltyChargesTable.charity
        )
    }</div>
                    <div class="form-field" style="left: 920px; top: 355px; width: 140px; text-align: right;">${
        formatCurrency(
            receipt.biltyChargesTable.handling
        )
    }</div>
                    <div class="form-field" style="left: 920px; top: 421px; width: 140px; text-align: right;">${
        formatCurrency(
            receipt.biltyChargesTable.delivery
        )
    }</div>
                    <div class="form-field" style="left: 920px; top: 490px; width: 140px; text-align: right;">${
        formatCurrency(
            receipt.biltyChargesTable.ddCharges
        )
    }</div>
                    <div class="form-field" style="left: 920px; top: 558px; width: 140px; text-align: right;">${
        formatCurrency(
            receipt.biltyChargesTable.demurrage
        )
    }</div>
                    <div class="form-field" style="left: 920px; top: 623px; width: 140px; text-align: right;">${
        formatCurrency(
            receipt.biltyChargesTable.otherCharges
        )
    }</div>
                    <div class="form-field" style="left: 920px; top: 664px; width: 140px; text-align: right; font-weight: bold; font-size: 26px;">${
        formatCurrency(
            receipt.biltyChargesTable.grandTotal
        )
    }</div>
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