package com.bilty.generator.bridge
import kotlin.js.Date

// Format: dd-MM-yyyy (as defined in Constants.DATE_FORMAT)
actual fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp.toDouble())
    val day = date.getDate().toInt().toString().padStart(2, '0')
    val month = (date.getMonth().toInt() + 1).toString().padStart(2, '0')
    val year = date.getFullYear().toInt()
    return "$day-$month-$year"
}