package com.bilty.generator.utils.helpers

import com.bilty.generator.model.constants.FirebaseConstants.Nodes.PrintQueue
import com.bilty.generator.model.constants.FirebaseConstants.Nodes.Fields
import com.bilty.generator.model.constants.FirebaseConstants.Nodes.Prefixes
import com.bilty.generator.model.data.PrintIndex
import com.bilty.generator.model.data.PrintJob
import com.bilty.generator.model.enums.PrintStatus
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Firebase helper for managing print queue with dual-node structure
 * 
 * Structure:
 * - printJobs/{fbNodeId} → Full print job data
 * - printIndex/{companyId}/{branchId}/{grNo} → Lightweight index entry
 * 
 * This helper provides low-level Firebase operations for the print queue system
 */
class FirebasePrintQueueHelper {
    private val database: FirebaseDatabase = Firebase.database

    /**
     * Adds a new print job OR updates existing one if GR already exists
     * Checks printIndex first to prevent duplicate jobs for the same GR
     * 
     * @param printJob The full print job data to add
     * @param onSuccess Callback with the Firebase node ID (new or existing)
     * @param onFailure Callback when operation fails
     */
    suspend fun addPrintJob(
        printJob: PrintJob,
        onSuccess: (fbNodeId: String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            println("🔵 FirebasePrintQueueHelper.addPrintJob: Starting for GR=${printJob.grNo}")
            
            // First, check if this GR already exists in printIndex
            val indexRef = database.reference(PrintQueue.PRINT_INDEX)
                .child("${Prefixes.COMPANY}${printJob.companyId}")
                .child("${Prefixes.BRANCH}${printJob.branchId}")
                .child(printJob.grNo)
            
            val existingSnapshot = indexRef.valueEvents.first()
            
            val fbNodeId: String
            
            if (existingSnapshot.exists) {
                // GR already exists - reuse the existing fbNodeId
                val existingIndex = existingSnapshot.value<PrintIndex>()
                fbNodeId = existingIndex.fbNodeId
                println("📍 GR already exists! Reusing fbNodeId: $fbNodeId")
                
                // Update the existing job in printJobs
                val jobRef = database.reference(PrintQueue.PRINT_JOBS).child(fbNodeId)
                jobRef.setValue(printJob)
                println("✅ Updated existing printJobs/$fbNodeId")
                
            } else {
                // GR does not exist - create a new job
                val printJobsRef = database.reference(PrintQueue.PRINT_JOBS)
                val newJobRef = printJobsRef.push()
                fbNodeId = newJobRef.key ?: throw Exception("Failed to generate Firebase node ID")
                
                println("📍 GR is new! Generated fbNodeId: $fbNodeId")
                
                // Write to printJobs/{fbNodeId}
                newJobRef.setValue(printJob)
                println("✅ Written to printJobs/$fbNodeId")
            }
            
            // Always update/create the index entry (in case status changed)
            val indexEntry = PrintIndex(
                fbNodeId = fbNodeId,
                status = printJob.status,
                timestamp = printJob.timestamp
            )
            
            indexRef.setValue(indexEntry)
            println("✅ Written to printIndex/${printJob.companyId}/${printJob.branchId}/${printJob.grNo}")
            
            onSuccess(fbNodeId)
            
        } catch (e: Exception) {
            println("❌ FirebasePrintQueueHelper.addPrintJob: Error - ${e.message}")
            e.printStackTrace()
            onFailure(e)
        }
    }

    /**
     * Updates the status of a print job in both nodes
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
        try {
            println("🔵 FirebasePrintQueueHelper.updatePrintStatus: fbNodeId=$fbNodeId, grNo=$grNo, status=$newStatus")
            
            // Update status in printJobs/{fbNodeId}/status
            val jobRef = database.reference(PrintQueue.PRINT_JOBS).child(fbNodeId)
            jobRef.child(Fields.STATUS).setValue(newStatus)
            println("✅ Updated printJobs/$fbNodeId/status")
            
            // Update status in printIndex/company_{id}/branch_{id}/{grNo}/status
            val indexRef = database.reference(PrintQueue.PRINT_INDEX)
                .child("${Prefixes.COMPANY}$companyId")
                .child("${Prefixes.BRANCH}$branchId")
                .child(grNo)
            indexRef.child(Fields.STATUS).setValue(newStatus)
            println("✅ Updated printIndex/$companyId/$branchId/$grNo/status")
            
            onSuccess()
            
        } catch (e: Exception) {
            println("❌ FirebasePrintQueueHelper.updatePrintStatus: Error - ${e.message}")
            e.printStackTrace()
            onFailure(e)
        }
    }

    /**
     * Deletes a print job from both printJobs and printIndex nodes
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
        try {
            println("🔵 FirebasePrintQueueHelper.deletePrintJob: fbNodeId=$fbNodeId, grNo=$grNo")
            
            // Delete from printJobs/{fbNodeId}
            val jobRef = database.reference(PrintQueue.PRINT_JOBS).child(fbNodeId)
            jobRef.removeValue()
            println("✅ Deleted printJobs/$fbNodeId")
            
            // Delete from printIndex/company_{id}/branch_{id}/{grNo}
            val indexRef = database.reference(PrintQueue.PRINT_INDEX)
                .child("${Prefixes.COMPANY}$companyId")
                .child("${Prefixes.BRANCH}$branchId")
                .child(grNo)
            indexRef.removeValue()
            println("✅ Deleted printIndex/$companyId/$branchId/$grNo")
            
            onSuccess()
            
        } catch (e: Exception) {
            println("❌ FirebasePrintQueueHelper.deletePrintJob: Error - ${e.message}")
            e.printStackTrace()
            onFailure(e)
        }
    }

    /**
     * Observes the print queue index for a specific company and branch
     * Returns a Flow that emits whenever the index changes
     * 
     * @param companyId The company ID to observe
     * @param branchId The branch ID to observe
     * @return Flow of Map<String, PrintIndex> where key is grNo
     */
    fun observePrintIndex(
        companyId: String,
        branchId: String
    ): Flow<Map<String, PrintIndex>> {
        println("🔵 FirebasePrintQueueHelper.observePrintIndex: company=$companyId, branch=$branchId")
        
        val indexRef = database.reference(PrintQueue.PRINT_INDEX)
            .child("${Prefixes.COMPANY}$companyId")
            .child("${Prefixes.BRANCH}$branchId")
        
        return indexRef.valueEvents
            .onStart {
                println("🔵 FirebasePrintQueueHelper: Observer started")
            }
            .cancellable()
            .map { snapshot ->
                println("📦 FirebasePrintQueueHelper: Received snapshot, exists=${snapshot.exists}")
                
                val indexMap = mutableMapOf<String, PrintIndex>()
                
                snapshot.children.forEach { child ->
                    val grNo = child.key ?: return@forEach
                    try {
                        val entry = child.value<PrintIndex>()
                        indexMap[grNo] = entry
                        println("  ✅ Parsed GR=$grNo, fbNodeId=${entry.fbNodeId}, status=${entry.status}")
                    } catch (e: Exception) {
                        println("  ❌ Failed to parse GR=$grNo: ${e.message}")
                    }
                }
                
                println("📦 FirebasePrintQueueHelper: Emitting ${indexMap.size} entries")
                indexMap.toMap()
            }
            .catch { error ->
                println("❌ FirebasePrintQueueHelper.observePrintIndex: Error - ${error.message}")
                emit(emptyMap())
            }
    }

    /**
     * Fetches a single print job by its Firebase node ID
     * 
     * @param fbNodeId The Firebase node ID
     * @return The PrintJob or null if not found
     */
    suspend fun getPrintJobById(fbNodeId: String): PrintJob? {
        return try {
            println("🔵 FirebasePrintQueueHelper.getPrintJobById: fbNodeId=$fbNodeId")
            
            val jobRef = database.reference(PrintQueue.PRINT_JOBS).child(fbNodeId)
            val snapshot = jobRef.valueEvents.first()
            
            if (!snapshot.exists) {
                println("⚠️ PrintJob not found for fbNodeId=$fbNodeId")
                return null
            }
            
            val printJob = snapshot.value<PrintJob>()
            println("✅ Fetched PrintJob for GR=${printJob.grNo}")
            printJob
            
        } catch (e: Exception) {
            println("❌ FirebasePrintQueueHelper.getPrintJobById: Error - ${e.message}")
            e.printStackTrace()
            null
        }
    }
}