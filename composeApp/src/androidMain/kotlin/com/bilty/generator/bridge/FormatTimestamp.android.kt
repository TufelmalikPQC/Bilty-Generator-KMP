package com.bilty.generator.bridge

import com.bilty.generator.model.constants.Constants.DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun formatTimestamp(timestamp: Long): String {
    val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}
