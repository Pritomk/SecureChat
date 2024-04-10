package com.example.securechat.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

object AppCommonMethods {
    fun formatTimeFromMillis(timeInMillis: Long?, format: String): String? {
        val formatter = SimpleDateFormat(format)
        formatter.timeZone = TimeZone.getDefault()
        return timeInMillis?.let { Date(it) }?.let { formatter.format(it) }
    }
}