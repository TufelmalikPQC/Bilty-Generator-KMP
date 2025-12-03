package com.bilty.generator.model.data

import kotlinx.serialization.Serializable

@Serializable
data class ReceiptCharges(
    val freight: Double? = 0.0,
    val charity: Double? = 0.0,
    val handling: Double? = 0.0,
    val delivery: Double? = 0.0,
    val ddCharge: Double? = 0.0,
    val demurrage: Double? = 0.0,
    val otherCost: Double? = 0.0,
    val grandTotal: Double? = 0.0
)