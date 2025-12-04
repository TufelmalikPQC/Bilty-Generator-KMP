package com.bilty.generator.modules.printqueue

import com.bilty.generator.model.data.PrintJob
import com.bilty.generator.model.data.PrintRequestData
import com.bilty.generator.model.enums.PrintStatus
import com.bilty.generator.utils.helpers.FirebasePrintQueueHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Repository for managing print queue operations
 * Provides a clean API for ViewModels to interact with the Firebase print queue
 * 
 * Uses dual-node structure:
 * - printJobs/{fbNodeId} → Full print job data
 * - printIndex/{companyId}/{branchId}/{grNo} → Fast lookup index
 * 
 * @property firebaseHelper Firebase helper for low-level operations
 */
class PrintQueueRepository {
    private val firebaseHelper = FirebasePrintQueueHelper()

    /**
     * Adds a new print job to the queue
     * Creates entries in both printJobs and printIndex nodes
     * 
     * @param grNo The GR number (unique identifier)
     * @param printData The complete print data
     * @param companyId The company ID
     * @param branchId The branch ID
     * @param onSuccess Callback with the generated Firebase node ID
     * @param onFailure Callback when operation fails
     */
    @OptIn(ExperimentalTime::class)
    suspend fun addToPrintQueue(
        grNo: String,
        printData: PrintRequestData,
        companyId: String,
        branchId: String,
        onSuccess: (fbNodeId: String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        println("🔵 PrintQueueRepository.addToPrintQueue: GR=$grNo, company=$companyId, branch=$branchId")
        
        val printJob = PrintJob(
            grNo = grNo,
            companyId = companyId,
            branchId = branchId,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            status = PrintStatus.PENDING,
            printData = printData
        )
        
        firebaseHelper.addPrintJob(
            printJob = printJob,
            onSuccess = { fbNodeId ->
                println("✅ PrintQueueRepository: Successfully added print job with ID=$fbNodeId")
                onSuccess(fbNodeId)
            },
            onFailure = { exception ->
                println("❌ PrintQueueRepository: Failed to add print job - ${exception.message}")
                onFailure(exception)
            }
        )
    }

    /**
     * Updates the status of a print job
     * Updates both printJobs and printIndex nodes atomically
     * 
     * @param fbNodeId The Firebase node ID in printJobs
     * @param grNo The GR number
     * @param companyId The company ID
     * @param branchId The branch ID
     * @param newStatus The new status to set
     * @param onSuccess Callback when update succeeds
     * @param onFailure Callback when update fails
     */
    suspend fun updatePrintStatus(
        fbNodeId: String,
        grNo: String,
        companyId: String,
        branchId: String,
        newStatus: PrintStatus,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        println("🔵 PrintQueueRepository.updatePrintStatus: fbNodeId=$fbNodeId, status=$newStatus")
        
        firebaseHelper.updatePrintStatus(
            fbNodeId = fbNodeId,
            grNo = grNo,
            companyId = companyId,
            branchId = branchId,
            newStatus = newStatus,
            onSuccess = {
                println("✅ PrintQueueRepository: Status updated successfully")
                onSuccess()
            },
            onFailure = { exception ->
                println("❌ PrintQueueRepository: Failed to update status - ${exception.message}")
                onFailure(exception)
            }
        )
    }

    /**
     * Deletes a print job from the queue
     * Removes from both printJobs and printIndex nodes
     * 
     * @param fbNodeId The Firebase node ID in printJobs
     * @param grNo The GR number
     * @param companyId The company ID
     * @param branchId The branch ID
     * @param onSuccess Callback when deletion succeeds
     * @param onFailure Callback when deletion fails
     */
    suspend fun deletePrintJob(
        fbNodeId: String,
        grNo: String,
        companyId: String,
        branchId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        println("🔵 PrintQueueRepository.deletePrintJob: fbNodeId=$fbNodeId, grNo=$grNo")
        
        firebaseHelper.deletePrintJob(
            fbNodeId = fbNodeId,
            grNo = grNo,
            companyId = companyId,
            branchId = branchId,
            onSuccess = {
                println("✅ PrintQueueRepository: Print job deleted successfully")
                onSuccess()
            },
            onFailure = { exception ->
                println("❌ PrintQueueRepository: Failed to delete print job - ${exception.message}")
                onFailure(exception)
            }
        )
    }

    /**
     * Observes the print queue for a specific company and branch
     * Fetches full print job data by reading index and then fetching from printJobs
     * 
     * Flow emits:
     * - When index changes (new job added, status updated, job deleted)
     * - List of all pending print jobs for the company/branch
     * 
     * @param companyId The company ID to observe
     * @param branchId The branch ID to observe
     * @return Flow of list of PrintJob with their Firebase node IDs in a Pair
     */
    fun observePrintQueue(
        companyId: String,
        branchId: String
    ): Flow<List<Pair<String, PrintJob>>> {
        println("🔵 PrintQueueRepository.observePrintQueue: company=$companyId, branch=$branchId")
        
        return firebaseHelper.observePrintIndex(companyId, branchId)
            .map { indexMap ->
                println("📦 PrintQueueRepository: Received index with ${indexMap.size} entries")
                
                val printJobs = mutableListOf<Pair<String, PrintJob>>()
                
                // Fetch full print job for each index entry
                indexMap.forEach { (grNo, indexEntry) ->
                    val printJob = firebaseHelper.getPrintJobById(indexEntry.fbNodeId)
                    if (printJob != null) {
                        printJobs.add(indexEntry.fbNodeId to printJob)
                        println("  ✅ Loaded full job for GR=$grNo")
                    } else {
                        println("  ⚠️ Could not load full job for GR=$grNo (fbNodeId=${indexEntry.fbNodeId})")
                    }
                }
                
                println("📦 PrintQueueRepository: Emitting ${printJobs.size} print jobs")
                printJobs.toList()
            }
            .catch { exception ->
                println("❌ PrintQueueRepository.observePrintQueue: Error - ${exception.message}")
                emit(emptyList())
            }
    }

    /**
     * Fetches a single print job by its Firebase node ID
     * 
     * @param fbNodeId The Firebase node ID
     * @return The PrintJob or null if not found
     */
    suspend fun getPrintJobById(fbNodeId: String): PrintJob? {
        println("🔵 PrintQueueRepository.getPrintJobById: fbNodeId=$fbNodeId")
        return firebaseHelper.getPrintJobById(fbNodeId)
    }
}