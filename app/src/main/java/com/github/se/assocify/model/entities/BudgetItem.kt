package com.github.se.assocify.model.entities

/**
 * Represents an item in the budget sheet For example some sweater in the category Logistics Pole
 *
 * @param uid unique identifier of the item
 * @param nameItem name of the item (for example: sweater)
 * @param amount amount of the item
 * @param tva TVA of the item
 * @param description description of the item
 * @param subcategoryUID category of the item (for example: Logistics Pole)
 */
data class BudgetItem(
    val uid: String,
    val nameItem: String,
    val amount: Int,
    val tva: TVA,
    val description: String,
    val subcategoryUID: String,
    val year: Int
) {
  fun getAmount(tvaActive: Boolean): Int {
    return if (tvaActive) this.amount + (this.amount * this.tva.rate / 100f).toInt()
    else this.amount
  }

  fun getFormattedDescription(): String {
    return this.description.ifEmpty { "-" }
  }
}
