package com.bilty.generator.model.reponse

import com.bilty.generator.model.data.PrintRequest
import com.bilty.generator.model.enums.PrintStatus
import kotlinx.serialization.Serializable

@Serializable
data class PrintRequestResponse(
    val status: PrintStatus? = PrintStatus.PENDING,
    val statusCode: Int = 200,
    val message: String = "",
    val data: PrintRequest? = PrintRequest()
)