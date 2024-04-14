package com.github.se.assocify.ui.util

/** Utility class for price related operations. */
object PriceUtil {

  /*
   * Regular expression for valid price characters.
   */
  const val VALID_CHARS = """^[0-9.]+$"""

  /**
   * Formats the given price to a string with two decimal places.
   *
   * @param price the price to format
   * @return the formatted price
   */
  fun formatPrice(price: Double): String {
    return "%.2f".format(price)
  }

  /**
   * Converts the given price to a double
   *
   * @param price the price string to convert
   * @return the double price
   */
  fun toDouble(price: String): Double {
    return price.toDouble()
  }

  /**
   * Converts the given price to cents
   *
   * @param price the price to format
   * @return the formatted price
   */
  fun toCents(price: String): Int {
    return (toDouble(price) * 100).toInt()
  }

  /**
   * Checks if the given price has invalid characters.
   *
   * @param price the price to check
   * @return true if the price has invalid characters, false otherwise
   */
  fun hasInvalidCharacters(price: String): Boolean {
    return price.isNotEmpty() &&
        (!price.matches(Regex(VALID_CHARS)) || price.count { it == '.' } > 1)
  }

  /**
   * Checks if the given price is zero.
   *
   * @param price the price to check
   * @return true if the price is zero or ".", false otherwise
   */
  fun isZero(price: String): Boolean {
    return price == "." || (price.toDoubleOrNull() == 0.0)
  }

  /**
   * Checks if the given price is too large.
   *
   * @param price the price to check
   * @return true if the price is too large, false otherwise
   */
  fun isTooLarge(price: String): Boolean {
    return price.toDouble() > 999999.99
  }

  /**
   * Checks if the given price has more than 2 decimal places
   *
   * @param price the price to check
   * @return true if the price is too precise, false otherwise
   */
  fun isTooPrecise(price: String): Boolean {
    val dot = price.indexOf('.')
    return if (dot == -1) false else price.length - dot > 3
  }

  /**
   * Checks if the given price is a valid double value
   *
   * @param price the price to check
   * @return true if the price is valid, false otherwise
   */
  fun isValid(price: String): Boolean {
    return price.toDoubleOrNull() != null && price.toDouble() >= 0
  }

  /**
   * Converts the given cents to a string price
   *
   * @param cents the cents to convert
   * @return the string price
   */
  fun fromCents(cents: Int): String {
    return "%.2f".format(cents.toDouble() / 100)
  }
}
