package com.github.se.assocify.ui.util

import java.time.format.DateTimeFormatter

/** Utility class for date conversion. The date format is dd/MM/yyyy. */
object DateUtil {

  /** String representing a null date. */
  const val NULL_DATE_STRING = "--/--/--"

  /**
   * Converts a date to a string. If the date is null, it returns a string representing a null date.
   */
  fun toString(date: java.time.LocalDate?): String {
    if (date == null) {
      return NULL_DATE_STRING
    }
    return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
  }

  /**
   * Converts a string to a date. If the string is empty or represents a null date, it returns null.
   */
  fun toDate(date: String): java.time.LocalDate? {
    if (date.isEmpty() || date == NULL_DATE_STRING) {
      return null
    }
    return java.time.LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
  }
}
