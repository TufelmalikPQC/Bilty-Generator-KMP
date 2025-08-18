package com.bilty.generator.model.data

import kotlinx.serialization.Serializable

@Serializable
data class RoadLineDeliveryReceipt(
    val receiptNumber: String = "",
    val branchName: String = "",
    val date: String = "",
    val consignee: String = "",
    val consignor: String = "",
    val biltyNumber: String = "",
    val biltyDate: String = "",
    val fromLocation: String = "",
    val packageCount: Int = 0,
    val particulars: String = "",
    val pMarka: String = "",
    val panNumber: String = "BGKPL8812J",
    val biltyChargesTable: BiltyChargesTable
)
