package com.bilty.generator.model.enums

// Enum for print options
enum class PrintOption(
    val title: String,
    val description: String
) {
    WITH_IMAGE(
        title = "Print with Receipt Image",
        description = "Include the receipt template background image"
    ),
    CONTENT_ONLY(
        title = "Print Content Only",
        description = "Print only text content without background receipt image"
    )
}
