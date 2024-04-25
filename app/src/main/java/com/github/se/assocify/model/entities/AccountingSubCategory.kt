package com.github.se.assocify.model.entities

/**
 * Represents the subcategory of an accounting item which is related to a category. For example,
 * Logistic Pole is a subcategory of the Pole category
 *
 * @param name name of the subcategory (ex: Logistics Pole)
 * @param category category of the subcategory (ex: Pole)
 * @param amount total amount of the subcategory
 */
data class AccountingSubCategory(
    val uid: String,
    val name: String,
    val category: AccountingCategory,
    val amount: Int
)
