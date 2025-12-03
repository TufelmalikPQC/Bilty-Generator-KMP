package com.bilty.generator.model.data

/**
 * Sealed class to represent send request status
 */
sealed class SendPrintResponseStatus {
    data object Idle : SendPrintResponseStatus()
    data object Loading : SendPrintResponseStatus()
    data class Success(val message: String) : SendPrintResponseStatus()
    data class Error(val error: String) : SendPrintResponseStatus()
}