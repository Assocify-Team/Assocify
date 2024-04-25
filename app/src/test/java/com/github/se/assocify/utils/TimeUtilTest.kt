package com.github.se.assocify.utils

import com.github.se.assocify.ui.util.TimeUtil
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

class TimeUtilTest {

  @Test
  fun testFormatTime() {
    val time = LocalTime.of(16, 40)
    val expected = "16:40"
    val actual = TimeUtil.toString(time)
    assertEquals(expected, actual)
  }

  @Test
  fun testToTime() {
    val time = "16:40"
    val expected = LocalTime.of(16, 40)
    val actual = TimeUtil.toTime(time)
    assertEquals(expected, actual)
  }
}
