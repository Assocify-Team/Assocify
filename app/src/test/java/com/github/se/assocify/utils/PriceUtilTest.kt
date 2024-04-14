package com.github.se.assocify.utils

import com.github.se.assocify.ui.util.PriceUtil
import org.junit.Assert.assertEquals
import org.junit.Test

class PriceUtilTest {

  @Test
  fun testFormatPrice() {
    val price = 100.0
    val expected = "100.00"
    val actual = PriceUtil.formatPrice(price)
    assertEquals(expected, actual)
  }

  @Test
  fun testToDouble() {
    val price = "100.00"
    val expected = 100.0
    val actual = PriceUtil.toDouble(price)
    assertEquals(expected, actual, 0.0)
  }

  @Test
  fun testToCents() {
    val price = "100.00"
    val expected = 10000
    val actual = PriceUtil.toCents(price)
    assertEquals(expected, actual)
  }

  @Test
  fun testHasInvalidCharacters() {
    val price = "100.00"
    val expected = false
    val actual = PriceUtil.hasInvalidCharacters(price)
    assertEquals(expected, actual)
  }

  @Test
  fun testIsZero() {
    val price = "100.00"
    val expected = false
    val actual = PriceUtil.isZero(price)
    assertEquals(expected, actual)
  }

  @Test
  fun testIsTooLarge() {
    val price = "100.00"
    val expected = false
    val actual = PriceUtil.isTooLarge(price)
    assertEquals(expected, actual)
  }

  @Test
  fun testIsTooPrecise() {
    val price = "100.00"
    val expected = false
    val actual = PriceUtil.isTooPrecise(price)
    assertEquals(expected, actual)
  }

  @Test
  fun testIsValid() {
    val price = "100.00"
    val expected = true
    val actual = PriceUtil.isValid(price)
    assertEquals(expected, actual)
  }

  @Test
  fun testFromCents() {
    val cents = 10000
    val expected = "100.00"
    val actual = PriceUtil.fromCents(cents)
    assertEquals(expected, actual)
  }
}
