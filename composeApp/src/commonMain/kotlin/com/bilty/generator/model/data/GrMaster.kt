package com.bilty.generator.model.data

import com.bilty.generator.model.enums.RateTypeEnum
import kotlinx.serialization.Serializable

@Serializable
data class GrMaster(
    val id: Long? = 0L,
    val grInfoId: Long? = 0L,
    val bookingId: Long? = 0L,
    val crossingId: Long? = 0L,
    val currentStatusId: Long? = 0L,
    val rateType: RateTypeEnum? = RateTypeEnum.T,
    val senderId: Long? = 0L,
    val receiverId: Long? = 0L,
    val originBranchId: Long? = 0L,
    val destinationBranchId: Long? = 0L,
    val destinationLocation: String? = "",
    val currentLocationBranchId: Long? = 0L,
    val bookingDate: String? = "",
    val deliveryDate: String? = "",
    val deliveryTypeId: Long? = 0L,
    val privateMark: String? = "",
    val remarks: String? = "",
    val isPodRequired: Boolean? = null,
    val podStatusId: Long? = 0L,
    val createdBy: Long? = 0L,
    val createdAt: String? = "",
    val updatedAt: String? = ""
)