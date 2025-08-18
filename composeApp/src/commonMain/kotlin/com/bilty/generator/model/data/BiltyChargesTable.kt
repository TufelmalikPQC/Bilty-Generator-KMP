package com.bilty.generator.model.data

import kotlinx.serialization.Serializable

@Serializable
data class BiltyChargesTable(
    val freight: Double = 0.0,
    val charity: Double = 0.0,
    val handling: Double = 0.0,
    val delivery: Double = 0.0,
    val ddCharges: Double = 0.0,
    val demurrage: Double = 0.0,
    val otherCharges: Double = 0.0,
    val grandTotal: Double = 0.0
)
