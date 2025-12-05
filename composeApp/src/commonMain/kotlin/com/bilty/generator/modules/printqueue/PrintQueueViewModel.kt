package com.bilty.generator.modules.printqueue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilty.generator.model.data.PrintJob
import com.bilty.generator.model.data.PrintRequestData
import com.bilty.generator.model.enums.PrintStatus
import com.bilty.generator.repository.PrintQueueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for managing print queue operations
 * Handles adding jobs to queue, updating status, deleting jobs, and observing queue changes
 */
class PrintQueueViewModel(
    private val repository: PrintQueueRepository
) : ViewModel() {

    // Static company and branch IDs (will be replaced with auth system later)
    private val currentCompanyId = "1"
    private val currentBranchId = "1"

    /**
     * StateFlow of print jobs in the queue
     * Key = Firebase node ID, Value = PrintJob
     */
    private val _printQueue = MutableStateFlow<List<Pair<String, PrintJob>>>(emptyList())
    val printQueue: StateFlow<List<Pair<String, PrintJob>>> = _printQueue.asStateFlow()

    /**
     * StateFlow for operation status (success/error messages)
     */
    private val _operationStatus = MutableStateFlow<PrintQueueOperationStatus>(PrintQueueOperationStatus.Idle)
    val operationStatus: StateFlow<PrintQueueOperationStatus> = _operationStatus.asStateFlow()

    init {
        // Start observing print queue on initialization
        startObservingQueue()
    }

    /**
     * Adds a print job to the queue
     * 
     * @param grNo The GR number
     * @param printData The complete print data
     */
    fun addToPrintQueue(grNo: String, printData: PrintRequestData) {
        println("🔵 PrintQueueViewModel.addToPrintQueue: GR=$grNo")
        
        viewModelScope.launch {
            _operationStatus.update { PrintQueueOperationStatus.Loading }
            
            repository.addToPrintQueue(
                grNo = grNo,
                printData = printData,
                companyId = currentCompanyId,
                branchId = currentBranchId,
                onSuccess = { fbNodeId ->
                    println("✅ PrintQueueViewModel: Print job added with ID=$fbNodeId")
                    _operationStatus.update { 
                        PrintQueueOperationStatus.Success("Print job added to queue for GR: $grNo") 
                    }
                },
                onFailure = { exception ->
                    println("❌ PrintQueueViewModel: Failed to add print job - ${exception.message}")
                    _operationStatus.update { 
                        PrintQueueOperationStatus.Error(exception.message ?: "Failed to add to print queue") 
                    }
                }
            )
        }
    }

    /**
     * Updates the status of a print job
     * 
     * @param fbNodeId The Firebase node ID
     * @param grNo The GR number
     * @param newStatus The new status to set
     */
    fun updatePrintStatus(fbNodeId: String, grNo: String, newStatus: PrintStatus) {
        println("🔵 PrintQueueViewModel.updatePrintStatus: fbNodeId=$fbNodeId, status=$newStatus")
        
        viewModelScope.launch {
            _operationStatus.update { PrintQueueOperationStatus.Loading }
            
            repository.updatePrintStatus(
                fbNodeId = fbNodeId,
                grNo = grNo,
                companyId = currentCompanyId,
                branchId = currentBranchId,
                newStatus = newStatus,
                onSuccess = {
                    println("✅ PrintQueueViewModel: Status updated successfully")
                    _operationStatus.update { 
                        PrintQueueOperationStatus.Success("Print status updated for GR: $grNo") 
                    }
                },
                onFailure = { exception ->
                    println("❌ PrintQueueViewModel: Failed to update status - ${exception.message}")
                    _operationStatus.update { 
                        PrintQueueOperationStatus.Error(exception.message ?: "Failed to update print status") 
                    }
                }
            )
        }
    }

    /**
     * Deletes a print job from the queue
     * 
     * @param fbNodeId The Firebase node ID
     * @param grNo The GR number
     */
    fun deletePrintJob(fbNodeId: String, grNo: String) {
        println("🔵 PrintQueueViewModel.deletePrintJob: fbNodeId=$fbNodeId, grNo=$grNo")
        
        viewModelScope.launch {
            _operationStatus.update { PrintQueueOperationStatus.Loading }
            
            repository.deletePrintJob(
                fbNodeId = fbNodeId,
                grNo = grNo,
                companyId = currentCompanyId,
                branchId = currentBranchId,
                onSuccess = {
                    println("✅ PrintQueueViewModel: Print job deleted successfully")
                    _operationStatus.update { 
                        PrintQueueOperationStatus.Success("Print job deleted for GR: $grNo") 
                    }
                },
                onFailure = { exception ->
                    println("❌ PrintQueueViewModel: Failed to delete print job - ${exception.message}")
                    _operationStatus.update { 
                        PrintQueueOperationStatus.Error(exception.message ?: "Failed to delete print job") 
                    }
                }
            )
        }
    }

    /**
     * Starts observing the print queue for the current company and branch
     * Automatically called on ViewModel initialization
     */
    private fun startObservingQueue() {
        println("🔵 PrintQueueViewModel.startObservingQueue: company=$currentCompanyId, branch=$currentBranchId")
        
        viewModelScope.launch {
            repository.observePrintQueue(
                companyId = currentCompanyId,
                branchId = currentBranchId
            )
            .catch { exception ->
                println("❌ PrintQueueViewModel: Error observing queue - ${exception.message}")
                _printQueue.update { emptyList() }
            }
            .collect { printJobs ->
                println("📦 PrintQueueViewModel: Received ${printJobs.size} print jobs")
                _printQueue.update { printJobs }
            }
        }
    }

    /**
     * Resets the operation status to Idle
     * Call this after showing success/error messages to user
     */
    fun resetOperationStatus() {
        _operationStatus.update { PrintQueueOperationStatus.Idle }
    }
}

/**
 * Sealed class representing the status of print queue operations
 */
sealed class PrintQueueOperationStatus {
    data object Idle : PrintQueueOperationStatus()
    data object Loading : PrintQueueOperationStatus()
    data class Success(val message: String) : PrintQueueOperationStatus()
    data class Error(val message: String) : PrintQueueOperationStatus()
}
