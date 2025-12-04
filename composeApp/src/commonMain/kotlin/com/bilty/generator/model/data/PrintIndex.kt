package com.bilty.generator.model.data

import com.bilty.generator.model.enums.PrintStatus
import kotlinx.serialization.Serializable

/**
 * Lightweight index entry stored in Firebase at: printIndex/{companyId}/{branchId}/{grNo}
 * Used for fast lookups and filtering print jobs by company/branch
 * Does NOT contain the full printData - only reference to the full job
 * 
 * @property fbNodeId Firebase reference ID pointing to the full print job in printJobs/{fbNodeId}
 * @property status Current status of the print job (synced with full job)
 * @property timestamp The Unix timestamp (milliseconds) when this job was created
 */
@Serializable
data class PrintIndex(
    val fbNodeId: String = "",
    val status: PrintStatus = PrintStatus.PENDING,
    val timestamp: Long = 0L
)
