package com.github.se.assocify.ui.util

import java.time.LocalTime
import java.time.format.DateTimeFormatter

/** Utility class for date conversion. The date format is dd/MM/yyyy. */
object TimeUtil {

    /** String representing a null time. */
    const val NULL_TIME_STRING = "--:--"

    /**
     * Converts time to a string. If the time is null, it returns a string representing a null time.
     */
    fun toString(time: java.time.LocalTime?): String {
        if (time == null) {
            return NULL_TIME_STRING
        }
        return time.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    /**
     * Converts a string to a time. If the string is empty or represents a null time, it returns null.
     */
    fun toDate(time: String): LocalTime? {
        if (time.isEmpty() || time == NULL_TIME_STRING) {
            return null
        }
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))
    }
}
