package com.bilty.generator.model.data

import com.bilty.generator.model.enums.PrintRequestResponseStatus
import kotlinx.serialization.Serializable

@Serializable
data class PrintRequestResponse(
    val status: PrintRequestResponseStatus? = PrintRequestResponseStatus.NOT_STARTED,
    val code: String = "",
    val message: String = "",
    val data: PrintRequest? = PrintRequest()
)