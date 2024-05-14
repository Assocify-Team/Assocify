package com.github.se.assocify.model.entities

import java.time.LocalDate

/**
 * Represents an item in the balance sheet. For example some sweater in the category Logistics Pole
 *
 * @param uid unique identifier of the item
 * @param nameItem name of the item (for example: sweater)
 * @param categoryUID unique identifier of the category of the item
 * @param receiptUID unique identifier of the receipt of the item
 * @param amount amount of the item
 * @param tva TVA of the item
 * @param description description of the item
 * @param date date of payment of the item
 * @param assignee entity to which the item is assigned (payer or receiver)
 * @param status status of the item (for example: paid, not paid)
 */
data class BalanceItem(
    val uid: String,
    val nameItem: String,
    val subcategoryUID: String,
    val receiptUID: String,
    val amount: Int, // unsigned: can be positive or negative
    val tva: TVA,
    val description: String,
    val date: LocalDate,
    val assignee: String,
    val status: Status,
)

/**
 * Represents the TVA of a budget or balance item, these types are also represented in the database
 * by
 *
 * @param rate the TVA rate
 */
enum class TVA(val rate: Float) {
  TVA_0(0.0f), // No VAT
  TVA_8(8.1f), // Normal rate
  TVA_2(2.6f), // Reduced rate
  TVA_3(3.8f); // Special housing rate

  override fun toString(): String {
    return (this.rate).toString() + "%"
  }

  companion object {
    fun floatToTVA(tva: Float): TVA {
      return when (tva) {
        TVA_0.rate -> TVA_0
        TVA_8.rate -> TVA_8
        TVA_2.rate -> TVA_2
        TVA_3.rate -> TVA_3
        else -> {
          throw IllegalArgumentException("Invalid TVA rate")
        }
      }
    }
  }
}
