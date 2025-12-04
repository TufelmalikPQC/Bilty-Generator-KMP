package com.bilty.generator.model.enums

/**
 * Status of a print job in the print queue system
 * PENDING - Print job is queued and waiting to be printed
 * COMPLETED - Print job was successfully printed
 * FAILED - Print job failed due to an error
 * CANCELLED - Print job was cancelled by user
 */
enum class PrintStatus {
    PENDING,
    PRINTING,
    COMPLETED,
    FAILED,
    CANCELLED
}