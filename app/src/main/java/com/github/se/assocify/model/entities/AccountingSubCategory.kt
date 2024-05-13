package com.github.se.assocify.model.entities

/**
 * Represents the subcategory of an accounting item which is related to a category. For example,
 * Logistic Pole is a subcategory of the Pole category
 *
 * @param uid uid of the subcategory
 * @param categoryUID uid of the category that the subcategory is related to (ex: Pole)
 * @param name name of the subcategory (ex: Logistic Pole)
 * @param amount total amount of the subcategory
 */
data class AccountingSubCategory(
    val uid: String,
    val categoryUID: String,
    val name: String,
    val amount: Int,
    val year: Int
)
