package com.github.se.assocify.ui.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId

object DateTimeUtil {
  /** Formats an OffsetDateTime to a string. */
  fun formatDateTime(offsetDateTime: OffsetDateTime): String {
    val localDateTime = toLocalDateTime(offsetDateTime)
    return DateUtil.formatDate(localDateTime.toLocalDate()) +
        " " +
        TimeUtil.formatTime(localDateTime.toLocalTime())
  }

  /** Converts an OffsetDateTime to a LocalDateTime. */
  fun toLocalDateTime(offsetDateTime: OffsetDateTime): LocalDateTime {
    return offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
  }

  /** Converts an OffsetDateTime to a LocalDate. */
  fun toLocalDate(offsetDateTime: OffsetDateTime): LocalDate {
    return offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDate()
  }

  /** Converts an OffsetDateTime to a LocalTime. */
  fun toLocalTime(offsetDateTime: OffsetDateTime): LocalTime {
    return offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalTime()
  }
}
