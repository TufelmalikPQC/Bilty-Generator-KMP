package com.bilty.generator.model.data

import kotlinx.serialization.Serializable

@Serializable
data class Branch(
    val id: Long? = 0L,
    val companyId: Long? = 0L,
    val branchCode: String? = "",
    val branchName: String? = "",
    val branchManagerId: Long? = 0L,
    val isActive: Boolean = true,
    val registrationDate: String? = "",
    val createdAt: String? = "",
    val updatedAt: String? = ""
)