package com.example.habittracker.utils

import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

object Calculations {
    fun calculateTimeBetweenDates(startDate: String): String {
        // Validate that the input date string is not empty
        if (startDate.isBlank()) {
            return "Invalid start date"
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val date1: Date
        val date2: Date

        try {
            date1 = sdf.parse(startDate) ?: return "Invalid date format"
            date2 = sdf.parse(timeStampToString(System.currentTimeMillis())) ?: return "Invalid current time"
        } catch (e: ParseException) {
            return "Date parsing error: ${e.message}"
        }

        var isNegative = false
        var difference = date2.time - date1.time
        if (difference < 0) {
            difference = -difference
            isNegative = true
        }

        val minutes = difference / 1000 / 60
        val hours = minutes / 60
        val days = hours / 24
        val months = days / (365 / 12)
        val years = days / 365

        return if (isNegative) {
            when {
                minutes < 240 -> "Starts in $minutes minutes"
                hours < 48 -> "Starts in $hours hours"
                days < 61 -> "Starts in $days days"
                months < 24 -> "Starts in $months months"
                else -> "Starts in $years years"
            }
        } else {
            when {
                minutes < 240 -> "$minutes minutes ago"
                hours < 48 -> "$hours hours ago"
                days < 61 -> "$days days ago"
                months < 24 -> "$months months ago"
                else -> "$years years ago"
            }
        }
    }

    private fun timeStampToString(timestamp: Long): String {
        val stamp = Timestamp(timestamp)
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        return sdf.format(Date(stamp.time))
    }

    fun cleanDate(_day: Int, _month: Int, _year: Int): String {
        val day = if (_day < 10) "0$_day" else _day.toString()
        val month = if (_month < 9) "0${_month + 1}" else (_month + 1).toString()
        return "$day/$month/$_year"
    }

    fun cleanTime(_hour: Int, _minute: Int): String {
        val hour = if (_hour < 10) "0$_hour" else _hour.toString()
        val minute = if (_minute < 10) "0$_minute" else _minute.toString()
        return "$hour:$minute"
    }
}
