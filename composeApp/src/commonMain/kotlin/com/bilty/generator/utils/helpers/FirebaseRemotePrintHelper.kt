package com.bilty.generator.utils.helpers

import com.bilty.generator.model.enums.PrintRequestResponseStatus
import com.bilty.generator.model.enums.PrintStatus
import com.bilty.generator.model.reponse.PrintRequestResponse
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.DatabaseReference
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class FirebaseRemotePrintHelper {
    val database: FirebaseDatabase = Firebase.database

    /**
     * Adds a new print request to Firebase
     * Path: companies/{companyId}/branches/{branchId}/print/{grNumber}
     * @param companyId The company ID
     * @param branchId The branch ID
     * @param grNumber The GR number (unique identifier for the print request)
     * @param printRequestResponse The print request response data
     * @param onSuccess Callback when request is added successfully
     * @param onFailure Callback when request addition fails
     */
    suspend fun addPrintRequest(
        companyId: String,
        branchId: String,
        grNumber: String,
        printRequestResponse: PrintRequestResponse,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val reference = returnPrintNodeReference(
                companyId = companyId,
                branchId = branchId,
                grNumber = grNumber
            )
            reference.setValue(printRequestResponse)
            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    /**
     * Updates the status of a print request
     * @param companyId The company ID
     * @param branchId The branch ID
     * @param grNumber The GR number
     * @param status The status to set (SUCCESS or FAILED)
     * @param statusCode The status code to set
     * @param message The message to set
     * @param updatePrintStatus Whether to update the print status to PRINTING (only for SUCCESS)
     * @param onSuccess Callback when update succeeds
     * @param onFailure Callback when update fails
     */
    suspend fun updatePrintRequestStatus(
        companyId: String,
        branchId: String,
        grNumber: String,
        status: PrintRequestResponseStatus,
        statusCode: Int,
        message: String,
        updatePrintStatus: Boolean = false,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        println("🔵 Repo.updatePrintRequestStatus: START → GR=$grNumber, status=$status")

        try {
            val reference = returnPrintNodeReference(companyId, branchId, grNumber)
            println("📍 Firebase path → ${reference.key}")

            // ✅ SINGLE READ using first()
            println("🔍 Reading existing data (single snapshot)…")
            val snapshot = reference.valueEvents.first()
            val existingData = snapshot.value<PrintRequestResponse>()
            println("📥 Existing data → $existingData")

            if (existingData.data?.grMasterId?.isEmpty() == true) {
                return onFailure(Exception("GR Master ID is empty for GR=$grNumber"))
            }

            // BUILD UPDATED OBJECT
            val updatedResponse = existingData.copy(
                status = status,
                statusCode = statusCode,
                message = message,
                data =
                    if (updatePrintStatus && status == PrintRequestResponseStatus.SUCCESS) {
                        println("🖨 Updating printStatus → PRINTING")
                        existingData.data?.copy(printStatus = PrintStatus.PRINTING)
                    } else {
                        existingData.data?.copy(printStatus = PrintStatus.CANCELLED)
                    }
            )

            println("📤 Writing updated data → $updatedResponse")
            reference.setValue(updatedResponse)

            println("✅ Write SUCCESS for GR=$grNumber")
            onSuccess()

        } catch (e: Exception) {
            println("❌ ERROR updating GR=$grNumber → ${e.message}")
            onFailure(e)
        }
    }


    /**
     * Observes print requests in real-time for a specific company and branch
     * Path: company_{companyId}/branch_{branchId}/print
     * @param companyId The company ID to filter by
     * @param branchId The branch ID to filter by
     * @return Flow of list of print request responses with their GR numbers
     */
    fun observePrintRequests(
        companyId: String,
        branchId: String
    ): Flow<List<Pair<String, PrintRequestResponse>>> {

        println("observePrintRequests: Subscribing → companyId=$companyId, branchId=$branchId")

        val reference = database
            .reference("company_$companyId")
            .child("branch_$branchId")
            .child("print")

        return reference.valueEvents
            .onStart {
                println("observePrintRequests: Firebase listener started")
            }
            .cancellable()
            .map { snapshot ->

                println("observePrintRequests: Data snapshot received → children=${snapshot.children.count()}")
                println("observePrintRequests: Snapshot exists=${snapshot.exists}")

                val printRequests = mutableListOf<Pair<String, PrintRequestResponse>>()

                snapshot.children.forEach { child ->
                    val grNumber = child.key ?: "null-key"
                    println("observePrintRequests: Processing GR=$grNumber")
                    println("observePrintRequests: Raw child data = ${child.value}")

                    val printRequest = try {
                        child.value<PrintRequestResponse>()
                    } catch (e: Exception) {
                        println("observePrintRequests: FAILED to parse GR=$grNumber → ${e.message}")
                        e.printStackTrace()
                        return@forEach
                    }
                    if (printRequest.data != null) {
                        println("  └─ Data.printStatus=${printRequest.data.printStatus}")
                        println("  └─ Data.printData is null? ${printRequest.data.printData == null}")
                    }

                    if (grNumber.isNotEmpty() &&
                        printRequest.status != PrintRequestResponseStatus.PRINTED
                    ) {
                        printRequests.add(grNumber to printRequest)
                        println("observePrintRequests: ✅ Added GR=$grNumber (status=${printRequest.status})")
                    } else {
                        println("observePrintRequests: ⏭️ Ignored GR=$grNumber (already printed or empty key)")
                    }
                }

                println("observePrintRequests: Emitting ${printRequests.size} pending items")

                printRequests.toList()
            }
            .catch { error ->
                println("observePrintRequests ERROR → ${error.message}")
                println("observePrintRequests: Emitting empty list due to error")
                emit(emptyList())
            }
    }


    /**
     * Updates the status of a print request to PRINTED
     * @param companyId The company ID
     * @param branchId The branch ID
     * @param grNumber The GR number
     * @param onSuccess Callback when update succeeds
     * @param onFailure Callback when update fails
     */
    suspend fun markAsPrinted(
        companyId: String,
        branchId: String,
        grNumber: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val reference = returnPrintNodeReference(
                companyId = companyId,
                branchId = branchId,
                grNumber = grNumber
            )

            // Get existing data first
            var existingData: PrintRequestResponse? = null
            reference.valueEvents.cancellable().collect { snapshot ->
                existingData = snapshot.value()
                return@collect
            }

            // Update with new values using data class
            val updatedResponse = existingData?.copy(
                status = PrintRequestResponseStatus.PRINTED,
                message = "Print completed successfully"
            ) ?: PrintRequestResponse(
                status = PrintRequestResponseStatus.PRINTED,
                message = "Print completed successfully"
            )

            reference.setValue(updatedResponse)
            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
        }
    }


    private fun returnPrintNodeReference(
        companyId: String,
        branchId: String,
        grNumber: String
    ): DatabaseReference {
        return database.reference("company_$companyId").child("branch_$branchId")
            .child("print").child(grNumber)
    }
}