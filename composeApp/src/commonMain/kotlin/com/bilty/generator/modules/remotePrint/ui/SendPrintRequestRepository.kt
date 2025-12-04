package com.bilty.generator.modules.remotePrint.ui

import com.bilty.generator.model.data.PrintJob
import com.bilty.generator.model.data.PrintRequest
import com.bilty.generator.model.data.PrintRequestData
import com.bilty.generator.model.enums.PrintStatus
import com.bilty.generator.model.reponse.PrintRequestResponse
import com.bilty.generator.modules.printqueue.PrintQueueRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Repository class for handling remote print operations with Firebase.
 * NOW USES DUAL-NODE STRUCTURE via PrintQueueRepository
 * 
 * This maintains the same API but internally uses:
 * - printJobs/{fbNodeId} for full data
 * - printIndex/{companyId}/{branchId}/{grNo} for fast lookup
 *
 * @property printQueueRepository Repository managing dual-node Firebase structure
 */
class SendPrintRequestRepository {

    private val printQueueRepository = PrintQueueRepository()
    
    // Map to store fbNodeId for each grNumber for status updates
    private val grToFbNodeMap = mutableMapOf<String, String>()

    /**
     * Sends a new print request to Firebase using dual-node structure
     * 
     * @param companyId The unique identifier of the company
     * @param branchId The unique identifier of the branch
     * @param grNumber The GR number - unique identifier for the print job
     * @param printRequest The print request data
     * @param onSuccess Callback invoked when the request is successfully added
     * @param onFailure Callback invoked when the request fails
     */
    suspend fun sendPrintRequest(
        companyId: String,
        branchId: String,
        grNumber: String,
        printRequest: PrintRequest,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        println("🔵 SendPrintRequestRepository.sendPrintRequest: GR=$grNumber")
        
        // Extract print data from PrintRequest
        val printData = printRequest.printData ?: PrintRequestData()
        
        // Add to dual-node structure via PrintQueueRepository
        printQueueRepository.addToPrintQueue(
            grNo = grNumber,
            printData = printData,
            companyId = companyId,
            branchId = branchId,
            onSuccess = { fbNodeId ->
                println("✅ SendPrintRequestRepository: Print request added with fbNodeId=$fbNodeId")
                // Store mapping for future status updates
                grToFbNodeMap[grNumber] = fbNodeId
                onSuccess()
            },
            onFailure = { exception ->
                println("❌ SendPrintRequestRepository: Failed to add print request - ${exception.message}")
                onFailure(exception)
            }
        )
    }

    /**
     * Updates the status of an existing print request
     * 
     * @param companyId The unique identifier of the company
     * @param branchId The unique identifier of the branch
     * @param grNumber The GR number to update
     * @param status The new status for the print request
     * @param statusCode HTTP-like status code
     * @param message Descriptive message about the status update
     * @param updatePrintStatus Whether to update the internal print status (legacy parameter, now mapped to status)
     * @param onSuccess Callback invoked when the update is successful
     * @param onFailure Callback invoked when the update fails
     */
    suspend fun updatePrintRequestStatus(
        companyId: String,
        branchId: String,
        grNumber: String,
        status: PrintStatus,
        statusCode: Int,
        message: String,
        updatePrintStatus: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        println("🔵 SendPrintRequestRepository.updatePrintRequestStatus: GR=$grNumber, status=$status")
        
        // Get fbNodeId from map or try to fetch from Firebase
        val fbNodeId = grToFbNodeMap[grNumber]
        
        if (fbNodeId == null) {
            println("⚠️ SendPrintRequestRepository: fbNodeId not found for GR=$grNumber, trying to fetch from index...")
            // Try to find it by observing once (this is a fallback)
            onFailure(Exception("Cannot update: fbNodeId not found for GR=$grNumber. Job might not exist."))
            return
        }
        
        // Update status in dual-node structure
        printQueueRepository.updatePrintStatus(
            fbNodeId = fbNodeId,
            grNo = grNumber,
            companyId = companyId,
            branchId = branchId,
            newStatus = status,
            onSuccess = {
                println("✅ SendPrintRequestRepository: Status updated successfully")
                onSuccess()
            },
            onFailure = { exception ->
                println("❌ SendPrintRequestRepository: Failed to update status - ${exception.message}")
                onFailure(exception)
            }
        )
    }

    /**
     * Approves a print request by updating its status to PRINTING
     * The job will be marked as PRINTING to indicate it's being processed
     * 
     * @param companyId The unique identifier of the company
     * @param branchId The unique identifier of the branch
     * @param grNumber The GR number to approve
     * @param statusCode Success status code (default: 200 - OK)
     * @param onSuccess Callback invoked when approval is successful
     * @param onFailure Callback invoked when approval fails
     */
    suspend fun approvePrintRequest(
        companyId: String,
        branchId: String,
        grNumber: String,
        statusCode: Int = 200,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        updatePrintRequestStatus(
            companyId = companyId,
            branchId = branchId,
            grNumber = grNumber,
            status = PrintStatus.PRINTING,  // Changed from COMPLETED to PRINTING
            statusCode = statusCode,
            message = "Print request approved and started",
            updatePrintStatus = true,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    /**
     * Rejects a print request by updating its status to CANCELLED
     * 
     * @param companyId The unique identifier of the company
     * @param branchId The unique identifier of the branch
     * @param grNumber The GR number to reject
     * @param statusCode Error status code (default: 400 - Bad Request)
     * @param message Rejection reason or message
     * @param onSuccess Callback invoked when rejection is successful
     * @param onFailure Callback invoked when rejection fails
     */
    suspend fun rejectPrintRequest(
        companyId: String,
        branchId: String,
        grNumber: String,
        statusCode: Int = 400,
        message: String = "Request rejected by user",
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        updatePrintRequestStatus(
            companyId = companyId,
            branchId = branchId,
            grNumber = grNumber,
            status = PrintStatus.CANCELLED,  // Changed from FAILED to CANCELLED
            statusCode = statusCode,
            message = message,
            updatePrintStatus = false,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    /**
     * Observes print requests in real-time for a specific company and branch
     * Returns data in the OLD format (PrintRequestResponse) for backward compatibility
     * 
     * @param companyId The unique identifier of the company to observe
     * @param branchId The unique identifier of the branch to observe
     * @return Flow emitting list of Pair<String, PrintRequestResponse>
     */
    fun observePrintRequests(
        companyId: String,
        branchId: String
    ): Flow<List<Pair<String, PrintRequestResponse>>> {
        println("🔵 SendPrintRequestRepository.observePrintRequests: company=$companyId, branch=$branchId")
        
        return printQueueRepository.observePrintQueue(companyId, branchId)
            .map { printJobs ->
                println("📦 SendPrintRequestRepository: Converting ${printJobs.size} PrintJobs to PrintRequestResponses")
                
                // Store fbNodeId mappings for future updates
                printJobs.forEach { (fbNodeId, printJob) ->
                    grToFbNodeMap[printJob.grNo] = fbNodeId
                }
                
                // Convert PrintJob to PrintRequestResponse format for backward compatibility
                printJobs.map { (fbNodeId, printJob) ->
                    val printRequest = PrintRequest(
                        printStatus = printJob.status,
                        printData = printJob.printData,
                        companyId = printJob.companyId,
                        branchId = printJob.branchId,
                        grMasterId = printJob.grNo
                    )
                    
                    val printRequestResponse = PrintRequestResponse(
                        status = printJob.status,
                        statusCode = 200,
                        message = "Print job retrieved",
                        data = printRequest
                    )
                    
                    printJob.grNo to printRequestResponse
                }
            }
            .catch { exception ->
                println("❌ SendPrintRequestRepository.observePrintRequests: Error - ${exception.message}")
                emit(emptyList())
            }
    }

    /**
     * Marks a print request as COMPLETED after successful printing
     * 
     * @param companyId The unique identifier of the company
     * @param branchId The unique identifier of the branch
     * @param grNumber The GR number to mark as printed
     * @param onSuccess Callback invoked when marking is successful
     * @param onFailure Callback invoked when marking fails
     */
    suspend fun markAsPrinted(
        companyId: String,
        branchId: String,
        grNumber: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        updatePrintRequestStatus(
            companyId = companyId,
            branchId = branchId,
            grNumber = grNumber,
            status = PrintStatus.COMPLETED,
            statusCode = 200,
            message = "Print completed successfully",
            updatePrintStatus = true,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}