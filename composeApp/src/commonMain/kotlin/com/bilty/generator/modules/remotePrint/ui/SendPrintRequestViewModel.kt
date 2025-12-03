package com.bilty.generator.modules.remotePrint.ui

import androidx.lifecycle.ViewModel
import com.bilty.generator.model.data.Branch
import com.bilty.generator.model.data.Company
import com.bilty.generator.model.data.GrMaster
import com.bilty.generator.model.enums.RateTypeEnum
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class SendPrintRequestViewModel : ViewModel() {

    private var _companies: MutableStateFlow<List<Company>> = MutableStateFlow(emptyList())
    val companies: StateFlow<List<Company>> = _companies.asStateFlow()

    private var _branches: MutableStateFlow<List<Branch>> = MutableStateFlow(listOf())
    val branches: StateFlow<List<Branch>> = _branches.asStateFlow()


    private var _grMasterList: MutableStateFlow<List<GrMaster>> = MutableStateFlow(listOf())
    val grMasterList: StateFlow<List<GrMaster>> = _grMasterList.asStateFlow()


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
                    grInfoId = "GR0001",
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
                    grInfoId = "GR0002",
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
                    grInfoId = "GR0003",
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
                    grInfoId = "GR0004",
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
                    grInfoId = "GR0005",
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
}