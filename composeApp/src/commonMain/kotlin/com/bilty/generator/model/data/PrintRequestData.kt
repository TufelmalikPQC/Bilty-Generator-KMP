package com.bilty.generator.model.data

import kotlinx.serialization.Serializable

@Serializable
data class PrintRequestData(
    val receiptNumber: String? = "",
    val branchName: String? = "",
    val receiptDate: Long? = 0L,
    val consignorName: String? = "",
    val consigneeName: String? = "",
    val biltyNo: String? = "",
    val biltyDate: Long? = 0L,
    val fromLocation: String? = "",
    val pkgs: Int? = 0,
    val particulars: String? = "",
    val signature: String? = "",
    val pMarka: String? = "",
    val receiptCharges: ReceiptCharges? = ReceiptCharges()
)