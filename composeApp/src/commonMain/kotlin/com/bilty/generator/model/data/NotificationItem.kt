package com.bilty.generator.model.data

import com.bilty.generator.model.enums.PrintStatus

data class NotificationItem(
    val companyName: String,
    val grNo: String,
    val timestamp: Long,
    val status: PrintStatus
)