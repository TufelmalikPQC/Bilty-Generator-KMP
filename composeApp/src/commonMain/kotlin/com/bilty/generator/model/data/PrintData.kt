package com.bilty.generator.model.data

import com.bilty.generator.model.enums.PrintFormat
import com.bilty.generator.model.enums.PrintStatus
import kotlinx.serialization.Serializable

@Serializable
data class PrintRequest(
    val printStatus: PrintStatus = PrintStatus.NOT_STARTED,
    val printerFormat: PrintFormat = PrintFormat.DOT_MATRIX,
    val printData: PrintRequestData? = PrintRequestData(),
    val companyId: String = "",
    val branchId: String = "",
    val grMasterId: String = "",
    val userId: String = ""
)