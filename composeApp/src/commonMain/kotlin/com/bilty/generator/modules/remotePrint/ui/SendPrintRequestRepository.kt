package com.bilty.generator.modules.remotePrint.ui

import com.bilty.generator.model.data.PrintRequest
import com.bilty.generator.model.enums.PrintRequestResponseStatus
import com.bilty.generator.model.reponse.PrintRequestResponse
import com.bilty.generator.utils.helpers.FirebaseRemotePrintHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

/**
 * Repository class for handling remote print operations with Firebase.
 * This class acts as a single source of truth for print-related data operations,
 * following the MVVM architecture pattern.
 *
 * Responsibilities:
 * - Manages communication with Firebase for print requests
 * - Provides methods for CRUD operations on print requests
 * - Handles data transformation and error handling
 * - Observes real-time updates from Firebase
 *
 * Design Pattern:
 * - Follows Repository pattern to abstract data source details from ViewModel
 * - All Firebase operations are delegated to FirebaseRemotePrintHelper
 * - Provides a clean API with suspend functions and Flow for reactive data
 *
 * @property firebasePrintHelper Helper class for Firebase Realtime Database operations
 */
class SendPrintRequestRepository {

    private val firebasePrintHelper = FirebaseRemotePrintHelper()

    /**
     * Sends a new print request to Firebase Realtime Database.
     * Creates a new print request node at path: company_{companyId}/branch_{branchId}/print/{grNumber}
     *
     * Use Case:
     * - Used when a user initiates a new print request from the sender app
     * - Creates initial request with NOT_STARTED status
     *
     * @param companyId The unique identifier of the company
     * @param branchId The unique identifier of the branch
     * @param grNumber The GR (Goods Receipt) number - unique identifier for the print job
     * @param printRequest The print request data containing all necessary printing information
     * @param onSuccess Callback invoked when the request is successfully added to Firebase
     * @param onFailure Callback invoked when the request fails, provides the exception for error handling
     */
    suspend fun sendPrintRequest(
        companyId: String,
        branchId: String,
        grNumber: String,
        printRequest: PrintRequest,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Create the print request response wrapper with initial status
        val printRequestResponse = PrintRequestResponse(
            status = PrintRequestResponseStatus.NOT_STARTED,
            statusCode = 200,
            message = "Print request created successfully",
            data = printRequest
        )

        // Delegate to Firebase helper to add the request
        firebasePrintHelper.addPrintRequest(
            companyId = companyId,
            branchId = branchId,
            grNumber = grNumber,
            printRequestResponse = printRequestResponse,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    /**
     * Updates the status of an existing print request in Firebase.
     * Reads existing data, updates specified fields, and writes back to Firebase.
     *
     * Use Case:
     * - Used to change request status (e.g., from NOT_STARTED to SUCCESS/FAILED)
     * - Updates print status when request is approved (sets to PRINTING)
     * - Updates print status when request is rejected (sets to CANCELLED)
     *
     * @param companyId The unique identifier of the company
     * @param branchId The unique identifier of the branch
     * @param grNumber The GR number to update
     * @param status The new status for the print request (SUCCESS, FAILED, etc.)
     * @param statusCode HTTP-like status code (e.g., 200 for success, 400 for client error, 500 for server error)
     * @param message Descriptive message about the status update (e.g., "Request approved successfully")
     * @param updatePrintStatus Whether to update the internal print status to PRINTING (true for approve, false for reject)
     * @param onSuccess Callback invoked when the update is successful
     * @param onFailure Callback invoked when the update fails, provides the exception
     */
    suspend fun updatePrintRequestStatus(
        companyId: String,
        branchId: String,
        grNumber: String,
        status: PrintRequestResponseStatus,
        statusCode: Int,
        message: String,
        updatePrintStatus: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebasePrintHelper.updatePrintRequestStatus(
            companyId = companyId,
            branchId = branchId,
            grNumber = grNumber,
            status = status,
            statusCode = statusCode,
            message = message,
            updatePrintStatus = updatePrintStatus,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    /**
     * Approves a print request by updating its status to SUCCESS.
     * This marks the request as approved and ready for printing.
     * Also updates the print status to PRINTING if updatePrintStatus is true.
     *
     * Use Case:
     * - Receiver app calls this when user approves a pending print request
     * - Changes status from NOT_STARTED to SUCCESS
     * - Triggers the actual printing process
     *
     * @param companyId The unique identifier of the company
     * @param branchId The unique identifier of the branch
     * @param grNumber The GR number to approve
     * @param statusCode Success status code (default: 200 - OK)
     * @param onSuccess Callback invoked when approval is successful
     * @param onFailure Callback invoked when approval fails, provides the exception
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
            status = PrintRequestResponseStatus.SUCCESS,
            statusCode = statusCode,
            message = "Request approved successfully",
            updatePrintStatus = true, // Set print status to PRINTING
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    /**
     * Rejects a print request by updating its status to FAILED.
     * This marks the request as rejected and will not be printed.
     * Sets the print status to CANCELLED.
     *
     * Use Case:
     * - Receiver app calls this when user rejects/cancels a print request
     * - Changes status from NOT_STARTED to FAILED
     * - Notifies sender that the request was rejected
     *
     * @param companyId The unique identifier of the company
     * @param branchId The unique identifier of the branch
     * @param grNumber The GR number to reject
     * @param statusCode Error status code (default: 400 - Bad Request)
     * @param message Rejection reason or message (e.g., "Printer offline", "Request rejected by user")
     * @param onSuccess Callback invoked when rejection is successful
     * @param onFailure Callback invoked when rejection fails, provides the exception
     */
    suspend fun rejectPrintRequest(
        companyId: String,
        branchId: String,
        grNumber: String,
        statusCode: Int = 400,
        message: String = "Request rejected",
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        updatePrintRequestStatus(
            companyId = companyId,
            branchId = branchId,
            grNumber = grNumber,
            status = PrintRequestResponseStatus.FAILED,
            statusCode = statusCode,
            message = message,
            updatePrintStatus = false, // Set print status to CANCELLED
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    /**
     * Observes print requests in real-time for a specific company and branch.
     * Returns a Flow that emits updates whenever print requests change in Firebase.
     * Automatically filters out requests with PRINTED status.
     *
     * Use Case:
     * - Receiver app uses this to listen for incoming print requests
     * - Provides real-time updates when new requests are added or existing ones are modified
     * - Filters completed (PRINTED) requests automatically
     *
     * Flow Behavior:
     * - Emits list of (GR Number, PrintRequestResponse) pairs
     * - Emits on initial subscription with current data
     * - Emits whenever data changes in Firebase
     * - Emits empty list on error (with error logged)
     * - Automatically cancels when collector is cancelled
     *
     * @param companyId The unique identifier of the company to observe
     * @param branchId The unique identifier of the branch to observe
     * @return Flow emitting list of Pair<String, PrintRequestResponse> where String is the GR number
     */
    fun observePrintRequests(
        companyId: String,
        branchId: String
    ): Flow<List<Pair<String, PrintRequestResponse>>> {
        return firebasePrintHelper.observePrintRequests(
            companyId = companyId,
            branchId = branchId
        ).catch { exception ->
            // Log error and emit empty list on failure to prevent crashes
            println("❌ Repository.observePrintRequests: Error - ${exception.message}")
            exception.printStackTrace()
            emit(emptyList())
        }
    }

    /**
     * Marks a print request as PRINTED after successful printing.
     * This is the final status in the print request lifecycle.
     *
     * Use Case:
     * - Receiver app calls this after successfully printing the document
     * - Removes the request from active/pending lists
     * - Provides audit trail of completed prints
     *
     * Lifecycle:
     * NOT_STARTED → SUCCESS (approved) → PRINTING → PRINTED (completed)
     * NOT_STARTED → FAILED (rejected/error)
     *
     * @param companyId The unique identifier of the company
     * @param branchId The unique identifier of the branch
     * @param grNumber The GR number to mark as printed
     * @param onSuccess Callback invoked when marking is successful
     * @param onFailure Callback invoked when marking fails, provides the exception
     */
    suspend fun markAsPrinted(
        companyId: String,
        branchId: String,
        grNumber: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebasePrintHelper.markAsPrinted(
            companyId = companyId,
            branchId = branchId,
            grNumber = grNumber,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}