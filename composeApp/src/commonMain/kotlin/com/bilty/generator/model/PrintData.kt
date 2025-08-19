package com.bilty.generator.model

/**
 * A sealed class representing the different types of content that can be printed.
 */
sealed class PrintData {
    /** Represents a PDF document from a ByteArray. */
    data class Pdf(val bytes: ByteArray) : PrintData() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Pdf

            return bytes.contentEquals(other.bytes)
        }

        override fun hashCode(): Int {
            return bytes.contentHashCode()
        }
    }

    /** Represents plain text content. */
    data class PlainText(val text: String) : PrintData()

    /** Represents text that requires a monospaced font to maintain formatting, like a dot matrix receipt. */
    data class DotMatrixText(val text: String) : PrintData()
}