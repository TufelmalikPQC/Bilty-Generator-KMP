package com.bilty.generator.model.reponse

import com.bilty.generator.model.data.PrintRequest
import com.bilty.generator.model.enums.PrintRequestResponseStatus
import kotlinx.serialization.Serializable

@Serializable
data class PrintRequestResponse(
    val status: PrintRequestResponseStatus? = PrintRequestResponseStatus.NOT_STARTED,
    val statusCode: Int = 200,
    val message: String = "",
    val data: PrintRequest? = PrintRequest()
)