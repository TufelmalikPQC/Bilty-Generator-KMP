package com.bilty.generator.model.enums

enum class RateTypeEnum(val originalName: String) {
    T(originalName = "To Pay"),
    P(originalName = "Paid"),
    TBB(originalName = "To Be Billed"),
    F(originalName = "Free")
}
