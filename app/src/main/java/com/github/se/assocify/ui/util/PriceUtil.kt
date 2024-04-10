package com.github.se.assocify.ui.util

object PriceUtil {

  const val ZERO_PRICE_STRING = "0.0"
  const val CHARS = """^[0-9.]+$"""

  fun toString(price: Double): String {
    /*TODO: Implement currency formatting and add appropriate checks*/
    return price.toString()
  }

  fun toDouble(price: String): Double {
    /*TODO: Remove - and add appropriate checks*/
    return price.toDouble()
  }

  fun hasInvalidCharacters(price: String): Boolean {
    return price.isNotEmpty() &&
            (!price.matches(Regex(CHARS))
            || price.count { it == '.' } > 1)
  }

  fun isZero(price: String): Boolean {
    return price == "." || (price.toDoubleOrNull() == 0.0)
  }

  fun isTooLarge(price: String): Boolean {
    return price.toDouble() > 999999.99
  }

  fun isTooPrecise(price: String): Boolean {
    val dot = price.indexOf('.')
    return if (dot == -1) false else price.length - dot > 3
  }

  fun isValid(price: String): Boolean {
    return price.toDoubleOrNull() != null && price.toDouble() >= 0
  }

}
