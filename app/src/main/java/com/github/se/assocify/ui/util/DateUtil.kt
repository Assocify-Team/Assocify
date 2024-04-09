package com.github.se.assocify.ui.util

import java.time.format.DateTimeFormatter

object DateUtil {

  const val NULL_DATE_STRING = "--/--/--"

  fun toString(date: java.time.LocalDate?): String {
    if (date == null) {
      return NULL_DATE_STRING
    }
    return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
  }

  fun toDate(date: String): java.time.LocalDate? {
    if (date.isEmpty() || date == NULL_DATE_STRING) {
      return null
    }
    return java.time.LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
  }
}
