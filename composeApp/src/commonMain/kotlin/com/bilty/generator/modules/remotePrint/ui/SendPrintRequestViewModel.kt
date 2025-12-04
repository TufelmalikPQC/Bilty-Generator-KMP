package com.bilty.generator.modules.remotePrint.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilty.generator.model.data.Branch
import com.bilty.generator.model.data.Company
import com.bilty.generator.model.data.GrMaster
import com.bilty.generator.model.data.NotificationItem
import com.bilty.generator.model.data.PrintRequest
import com.bilty.generator.model.data.SendPrintResponseStatus
import com.bilty.generator.model.enums.PrintRequestResponseStatus
import com.bilty.generator.model.enums.RateTypeEnum
import com.bilty.generator.utils.extention.toNotificationItems
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class SendPrintRequestViewModel : ViewModel() {

    // Repository for handling remote print operations
    private val repository = SendPrintRequestRepository()
    lateinit var approvePrintRequestJob: Job
    lateinit var rejectPrintRequestJob: Job


    private var _companies: MutableStateFlow<List<Company>> = MutableStateFlow(emptyList())
    val companies: StateFlow<List<Company>> = _companies.asStateFlow()

    private var _branches: MutableStateFlow<List<Branch>> = MutableStateFlow(listOf())
    val branches: StateFlow<List<Branch>> = _branches.asStateFlow()

    private var _grMasterList: MutableStateFlow<List<GrMaster>> = MutableStateFlow(listOf())
    val grMasterList: StateFlow<List<GrMaster>> = _grMasterList.asStateFlow()

    // Send status state
    private var _sendPrintStatusCode: MutableStateFlow<SendPrintResponseStatus> =
        MutableStateFlow(SendPrintResponseStatus.Idle)
    val sendPrintStatusCode: StateFlow<SendPrintResponseStatus> = _sendPrintStatusCode.asStateFlow()


    /*
        Observed print requests state
        First = grNumber
        Second = PrintRequestResponse()
    */
    private var _observedPrintRequests: MutableStateFlow<List<NotificationItem>> =
        MutableStateFlow(emptyList())
    val observedPrintRequests: StateFlow<List<NotificationItem>> =
        _observedPrintRequests.asStateFlow()


    init {
        initCompanyDetails()
        initBranchDetails()
        initGrDetails()
    }

    @OptIn(ExperimentalTime::class)
    fun initCompanyDetails() {
        if (companies.value.isNotEmpty()) return

        _companies.update {
            listOf(
                Company(
                    id = 1,
                    companyName = "Alpha Traders",
                    companyLogo = "https://picsum.photos/200",
                    registrationDate = getTimeMillis(),
                    adminId = "admin_1",
                    isActive = true,
                    isDefault = true,
                    createdAt = Clock.System.now().toEpochMilliseconds(),
                    updatedAt = Clock.System.now().toEpochMilliseconds()
                ),
                Company(
                    id = 2,
                    companyName = "Beta Logistics",
                    companyLogo = "https://picsum.photos/200",
                    registrationDate = Clock.System.now().toEpochMilliseconds(),
                    adminId = "admin_2",
                    isActive = true,
                    isDefault = false,
                    createdAt = Clock.System.now().toEpochMilliseconds(),
                    updatedAt = Clock.System.now().toEpochMilliseconds()
                ),
                Company(
                    id = 3,
                    companyName = "Gamma Industries",
                    companyLogo = "https://picsum.photos/200",
                    registrationDate = Clock.System.now().toEpochMilliseconds(),
                    adminId = "admin_3",
                    isActive = true,
                    isDefault = false,
                    createdAt = Clock.System.now().toEpochMilliseconds(),
                    updatedAt = Clock.System.now().toEpochMilliseconds()
                ),
                Company(
                    id = 4,
                    companyName = "Delta Services",
                    companyLogo = "https://picsum.photos/200",
                    registrationDate = Clock.System.now().toEpochMilliseconds(),
                    adminId = "admin_4",
                    isActive = true,
                    isDefault = false,
                    createdAt = Clock.System.now().toEpochMilliseconds(),
                    updatedAt = Clock.System.now().toEpochMilliseconds()
                ),
                Company(
                    id = 5,
                    companyName = "Epsilon Corp",
                    companyLogo = "https://picsum.photos/200",
                    registrationDate = Clock.System.now().toEpochMilliseconds(),
                    adminId = "admin_5",
                    isActive = true,
                    isDefault = false,
                    createdAt = Clock.System.now().toEpochMilliseconds(),
                    updatedAt = Clock.System.now().toEpochMilliseconds()
                )
            )
        }
    }

    fun initBranchDetails() {
        if (branches.value.isNotEmpty()) return

        _branches.update {
            listOf(
                Branch(
                    id = 1,
                    companyId = 10,
                    branchCode = "BR001",
                    branchName = "Main Branch",
                    branchManagerId = 101,
                    isActive = true,
                    registrationDate = "2024-01-10",
                    createdAt = "2024-01-10",
                    updatedAt = "2024-05-01"
                ),
                Branch(
                    id = 2,
                    companyId = 10,
                    branchCode = "BR002",
                    branchName = "North Hub",
                    branchManagerId = 102,
                    isActive = true,
                    registrationDate = "2024-02-12",
                    createdAt = "2024-02-12",
                    updatedAt = "2024-05-05"
                ),
                Branch(
                    id = 3,
                    companyId = 10,
                    branchCode = "BR003",
                    branchName = "South Hub",
                    branchManagerId = 103,
                    isActive = true,
                    registrationDate = "2024-03-18",
                    createdAt = "2024-03-18",
                    updatedAt = "2024-05-09"
                ),
                Branch(
                    id = 4,
                    companyId = 12,
                    branchCode = "BR004",
                    branchName = "East Depot",
                    branchManagerId = 104,
                    isActive = false,
                    registrationDate = "2024-04-22",
                    createdAt = "2024-04-22",
                    updatedAt = "2024-06-01"
                ),
                Branch(
                    id = 5,
                    companyId = 12,
                    branchCode = "BR005",
                    branchName = "West Depot",
                    branchManagerId = 105,
                    isActive = true,
                    registrationDate = "2024-05-30",
                    createdAt = "2024-05-30",
                    updatedAt = "2024-06-15"
                )
            )
        }
    }

    fun initGrDetails() {
        if (grMasterList.value.isNotEmpty()) return

        _grMasterList.update {
            listOf(
                GrMaster(
                    id = 1,
                    grInfoId = "GR_0001",
                    bookingId = 101,
                    crossingId = 501,
                    currentStatusId = 1,
                    rateType = RateTypeEnum.T,
                    senderId = 2001,
                    receiverId = 3001,
                    originBranchId = 10,
                    destinationBranchId = 20,
                    destinationLocation = "Mumbai",
                    currentLocationBranchId = 12,
                    bookingDate = "2025-01-02",
                    deliveryDate = "2025-01-05",
                    deliveryTypeId = 1,
                    privateMark = "Fragile",
                    remarks = "Handle carefully",
                    isPodRequired = true,
                    podStatusId = 2,
                    createdBy = 9001,
                    createdAt = "2025-01-02 09:45",
                    updatedAt = "2025-01-03 10:12"
                ),
                GrMaster(
                    id = 2,
                    grInfoId = "GR_0002",
                    bookingId = 102,
                    crossingId = 502,
                    currentStatusId = 2,
                    rateType = RateTypeEnum.P,
                    senderId = 2002,
                    receiverId = 3002,
                    originBranchId = 11,
                    destinationBranchId = 21,
                    destinationLocation = "Delhi",
                    currentLocationBranchId = 13,
                    bookingDate = "2025-01-04",
                    deliveryDate = "2025-01-07",
                    deliveryTypeId = 2,
                    privateMark = "",
                    remarks = "No issues",
                    isPodRequired = false,
                    podStatusId = 1,
                    createdBy = 9001,
                    createdAt = "2025-01-04 14:20",
                    updatedAt = "2025-01-05 08:31"
                ),
                GrMaster(
                    id = 3,
                    grInfoId = "GR_0003",
                    bookingId = 103,
                    crossingId = 503,
                    currentStatusId = 3,
                    rateType = RateTypeEnum.T,
                    senderId = 2003,
                    receiverId = 3003,
                    originBranchId = 15,
                    destinationBranchId = 25,
                    destinationLocation = "Ahmedabad",
                    currentLocationBranchId = 16,
                    bookingDate = "2025-01-06",
                    deliveryDate = "",
                    deliveryTypeId = 3,
                    privateMark = "Glass",
                    remarks = "",
                    isPodRequired = true,
                    podStatusId = 1,
                    createdBy = 9002,
                    createdAt = "2025-01-06 11:10",
                    updatedAt = "2025-01-06 18:05"
                ),
                GrMaster(
                    id = 4,
                    grInfoId = "GR_0004",
                    bookingId = 104,
                    crossingId = 504,
                    currentStatusId = 1,
                    rateType = RateTypeEnum.TBB,
                    senderId = 2004,
                    receiverId = 3004,
                    originBranchId = 18,
                    destinationBranchId = 28,
                    destinationLocation = "Surat",
                    currentLocationBranchId = 19,
                    bookingDate = "2025-01-08",
                    deliveryDate = "",
                    deliveryTypeId = 2,
                    privateMark = "",
                    remarks = "Urgent",
                    isPodRequired = false,
                    podStatusId = 1,
                    createdBy = 9003,
                    createdAt = "2025-01-08 09:00",
                    updatedAt = "2025-01-08 12:44"
                ),
                GrMaster(
                    id = 5,
                    grInfoId = "GR_0005",
                    bookingId = 105,
                    crossingId = 505,
                    currentStatusId = 4,
                    rateType = RateTypeEnum.P,
                    senderId = 2005,
                    receiverId = 3005,
                    originBranchId = 20,
                    destinationBranchId = 30,
                    destinationLocation = "Jaipur",
                    currentLocationBranchId = 22,
                    bookingDate = "2025-01-10",
                    deliveryDate = "2025-01-12",
                    deliveryTypeId = 1,
                    privateMark = "Heavy Load",
                    remarks = "",
                    isPodRequired = true,
                    podStatusId = 3,
                    createdBy = 9001,
                    createdAt = "2025-01-10 10:50",
                    updatedAt = "2025-01-11 09:15"
                )
            )
        }
    }

    /**
     * Sends a print request to Firebase via the repository
     * @param companyId The selected company ID
     * @param branchId The selected branch ID
     * @param grNumber The GR number
     * @param printRequest The print request data to send
     */
    fun sendPrintRequest(
        companyId: String,
        branchId: String,
        grNumber: String,
        printRequest: PrintRequest
    ) {
        viewModelScope.launch {
            println("🔵 ViewModel: Starting print request for GR: $grNumber")
            // Set loading state
            _sendPrintStatusCode.update { SendPrintResponseStatus.Loading }
            println("🔵 ViewModel: Status set to Loading")

            // Send request via repository
            println("🔵 ViewModel: Sending via repository...")
            repository.sendPrintRequest(
                companyId = companyId,
                branchId = branchId,
                grNumber = grNumber,
                printRequest = printRequest,
                onSuccess = {
                    println("✅ ViewModel: Repository success callback triggered")
                    _sendPrintStatusCode.update {
                        SendPrintResponseStatus.Success("Print request sent successfully for GR: $grNumber")
                    }
                    println("✅ ViewModel: Status updated to Success")
                },
                onFailure = { exception ->
                    println("❌ ViewModel: Repository failure callback triggered: ${exception.message}")
                    _sendPrintStatusCode.update {
                        SendPrintResponseStatus.Error(
                            exception.message ?: "Failed to send print request"
                        )
                    }
                    println("❌ ViewModel: Status updated to Error")
                }
            )
        }
    }


    /**
     * Starts observing print requests for a specific company and branch via the repository
     * @param companyId The company ID to observe
     * @param branchId The branch ID to observe
     */
    fun startObserving(companyId: String, branchId: String) {
        println("🔵 ViewModel.startObserving: Starting observation for company=$companyId, branch=$branchId")
        viewModelScope.launch {
            repository.observePrintRequests(
                companyId = companyId,
                branchId = branchId
            ).catch { exception ->
                // Handle error - emit empty list or log error
                println("❌ ViewModel.startObserving: Error occurred - ${exception.message}")
                _observedPrintRequests.update { emptyList() }
            }.collect { printRequests ->
                println("📦 ViewModel.startObserving: Received ${printRequests.size} raw items from repository")
                val notificationItems = printRequests.toNotificationItems()
                println("📦 ViewModel.startObserving: Converted to ${notificationItems.size} notification items")
                println("📦 ViewModel.startObserving: Items = ${notificationItems.map { it.grNo }}")
                _observedPrintRequests.update { notificationItems }
                println("✅ ViewModel.startObserving: StateFlow updated. Current value size = ${_observedPrintRequests.value.size}")
            }
        }
    }


    /**
     * Private function to update print request status via the repository
     */
    private fun updatePrintRequestStatus(
        companyId: String,
        branchId: String,
        grNumber: String,
        status: PrintRequestResponseStatus,
        statusCode: Int,
        message: String,
        updatePrintStatus: Boolean,
        successMessage: String,
        errorMessage: String,
        jobReference: Job?
    ): Job {
        println("🔵 ViewModel.updatePrintRequestStatus: Starting for GR: $grNumber, status: $status, updatePrintStatus: $updatePrintStatus")
        jobReference?.cancel()

        return viewModelScope.launch {
            println("🔵 ViewModel.updatePrintRequestStatus: Setting Loading state")
            _sendPrintStatusCode.update { SendPrintResponseStatus.Loading }

            repository.updatePrintRequestStatus(
                companyId = companyId,
                branchId = branchId,
                grNumber = grNumber,
                status = status,
                statusCode = statusCode,
                message = message,
                updatePrintStatus = updatePrintStatus,
                onSuccess = {
                    println("✅ ViewModel.updatePrintRequestStatus: Success callback - $successMessage")
                    _sendPrintStatusCode.value = SendPrintResponseStatus.Success(successMessage)
                },
                onFailure = { exception ->
                    println("❌ ViewModel.updatePrintRequestStatus: Failure callback - ${exception.message}")
                    _sendPrintStatusCode.value =
                        SendPrintResponseStatus.Error(exception.message ?: errorMessage)
                }
            )
        }
    }

    /**
     * Approves a print request via the repository
     * @param companyId The company ID
     * @param branchId The branch ID
     * @param grNumber The GR number to approve
     * @param statusCode Success code (default: 200)
     */
    fun approvePrintRequest(
        companyId: String,
        branchId: String,
        grNumber: String,
        statusCode: Int = 200
    ) {
        println("🟢 ViewModel.approvePrintRequest: Called for GR: $grNumber, company: $companyId, branch: $branchId")
        approvePrintRequestJob = updatePrintRequestStatus(
            companyId = companyId,
            branchId = branchId,
            grNumber = grNumber,
            status = PrintRequestResponseStatus.SUCCESS,
            statusCode = statusCode,
            message = "Request approved successfully",
            updatePrintStatus = true,
            successMessage = "Print request approved for GR: $grNumber",
            errorMessage = "Failed to approve print request",
            jobReference = if (::approvePrintRequestJob.isInitialized) approvePrintRequestJob else null
        )
    }

    /**
     * Rejects a print request via the repository
     * @param companyId The company ID
     * @param branchId The branch ID
     * @param grNumber The GR number to reject
     * @param code Error code (default: 400)
     * @param message Rejection message
     */
    fun rejectPrintRequest(
        companyId: String,
        branchId: String,
        grNumber: String,
        code: Int = 400,
        message: String = "Request rejected"
    ) {
        println("🔴 ViewModel.rejectPrintRequest: Called for GR: $grNumber, company: $companyId, branch: $branchId")
        rejectPrintRequestJob = updatePrintRequestStatus(
            companyId = companyId,
            branchId = branchId,
            grNumber = grNumber,
            status = PrintRequestResponseStatus.FAILED,
            statusCode = code,
            message = message,
            updatePrintStatus = false,
            successMessage = "Print request rejected for GR: $grNumber",
            errorMessage = "Failed to reject print request",
            jobReference = if (::approvePrintRequestJob.isInitialized) approvePrintRequestJob else null
        )
    }


}