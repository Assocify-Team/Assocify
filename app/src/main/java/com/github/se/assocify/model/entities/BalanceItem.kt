package com.github.se.assocify.model.entities


/**
 * Represents an item in the balance sheet.
 * For example some sweater in the category Logistics Pole
 * @param uid unique identifier of the item
 * @param nameItem name of the item (for example: sweater)
 * @param amount amount of the item
 * @param tva TVA of the item
 * @param description description of the item
 * @param category category of the item (for example: Logistics Pole)
 * @param date date of payment of the item
 * @param receipt receipt of the item
 * @param assignee user who is assigned to pay this item or to receive it
 * @param status status of the item (for example: paid, not paid)
 */
data class BalanceItem(
    val uid: String,
    val nameItem: String,
    val amount: Int, //unsigned: can be positive or negative
    val tva: TVA,
    val description: String,
    val category: AccountingSubCategory,
    val date: String,
    val receipt: Receipt?,
    val assignee: User?,
    val status: Phase
)

/**
 * Represents the TVA of a budget or balance item
 * @param rate the TVA rate
 */
enum class TVA(val rate: Double) {
    TVA_0(0.0),    // No VAT
    TVA_8(8.1),    // Normal rate
    TVA_2(2.6),    // Reduced rate
    TVA_3(3.8)     // Special housing rate
}
