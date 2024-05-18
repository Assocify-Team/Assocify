package com.github.se.assocify.ui.util

import java.time.LocalTime
import java.time.format.DateTimeFormatter

object DurationUtil {

  const val NULL_DURATION_STRING = "0h"

  /**
   * Converts duration to a string. If the time is null, it returns a string representing a null
   * time.
   */
  fun formatTime(time: LocalTime?): String {
    if (time == null) {
      return NULL_DURATION_STRING
    }
    return time.format(DateTimeFormatter.ofPattern("HH:mm"))
  }

  /**
   * Converts duration to a string. If the time is null, it returns a string representing a null
   * time.
   */
  fun formatTime(time: Int): String {
    val localTime = LocalTime.of(time / 60, time % 60)
    return localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
  }

  /**
   * Converts a string to a time. If the string is empty or represents a null time, it returns null.
   */
  fun toTime(time: String): LocalTime? {
    if (time.isEmpty() || time == NULL_DURATION_STRING) {
      return null
    }
    return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))
  }

  /**
   * Converts a string to a duration in minutes. If the string is empty or represents a null time,
   * it returns 0.
   */
  fun toDuration(time: String): Int {
    if (time.isEmpty() || time == NULL_DURATION_STRING) {
      return 0
    }
    val localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))
    return localTime.hour * 60 + localTime.minute
  }
}
