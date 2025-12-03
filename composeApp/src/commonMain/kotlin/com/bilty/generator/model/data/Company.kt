package com.bilty.generator.model.data

import kotlinx.serialization.Serializable

@Serializable
data class Company(
    val id: Long? = null,
    val companyName: String? = "",
    val companyLogo: String? = "",
    val registrationDate: Long? = 0L,
    val adminId: String? = "",
    val isActive: Boolean? = true,
    val isDefault: Boolean? = false,
    val createdAt: Long? = 0L,
    val updatedAt: Long? = 0L
)