package com.bilty.generator.model.data

import com.bilty.generator.model.enums.PrintStatus
import kotlinx.serialization.Serializable

/**
 * Full print job data stored in Firebase at: printJobs/{fbNodeId}
 * Contains all information needed to print without querying other tables
 * 
 * @property grNo The GR (Goods Receipt) number - unique identifier for this print job
 * @property companyId The company ID this print job belongs to
 * @property branchId The branch ID this print job belongs to
 * @property timestamp The Unix timestamp (milliseconds) when this job was created
 * @property status Current status of the print job
 * @property printData Complete print data including receipt details and charges
 */
@Serializable
data class PrintJob(
    val grNo: String = "",
    val companyId: String = "",
    val branchId: String = "",
    val timestamp: Long = 0L,
    val status: PrintStatus = PrintStatus.PENDING,
    val printData: PrintRequestData = PrintRequestData()
)
