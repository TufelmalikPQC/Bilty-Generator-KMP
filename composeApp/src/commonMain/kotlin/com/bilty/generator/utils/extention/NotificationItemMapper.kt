package com.bilty.generator.utils.extention

import com.bilty.generator.model.data.NotificationItem
import com.bilty.generator.model.reponse.PrintRequestResponse
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun List<Pair<String, PrintRequestResponse>>.toNotificationItems(): List<NotificationItem> {
    println("toNotificationItems: Converting ${this.size} items")
    return map { (grNo, response) ->
        println("toNotificationItems: GR=$grNo, response.data=${response.data}, status=${response.status}")

        val printRequest = response.data
        val companyName = printRequest?.printData?.branchName ?: "Unknown Company"
        val timestamp = printRequest?.printData?.receiptDate ?: Clock.System.now().toEpochMilliseconds()
        val status = printRequest?.printStatus ?: com.bilty.generator.model.enums.PrintStatus.NOT_STARTED

        println("toNotificationItems: Creating NotificationItem for GR=$grNo (company=$companyName, status=$status)")

        NotificationItem(
            companyName = companyName,
            grNo = grNo,
            timestamp = timestamp,
            status = status
        )
    }.also {
        println("toNotificationItems: Result size = ${it.size}")
    }
}