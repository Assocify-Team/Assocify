package com.github.se.assocify.model.entities

/**
 * Represents the subcategory of an accounting item which is related to a category. For example,
 * Logistic Pole is a subcategory of the Pole category
 */
data class AccountingSubCategory(val name: String, val category: AccountingCategory)

// TODO: list of accountingSubcategory should be in db:
// admin or presidence should be able to add new subcategories or modify it
val accountingSubCategories =
    listOf(
        AccountingSubCategory("Game", AccountingCategory.POLE),
        AccountingSubCategory("Logistic Pole", AccountingCategory.POLE),
        AccountingSubCategory("Communication Pole", AccountingCategory.POLE),
        AccountingSubCategory("ICBD", AccountingCategory.EVENT),
        AccountingSubCategory("SDF", AccountingCategory.EVENT),
        AccountingSubCategory("Game*", AccountingCategory.COMMISSION),
        AccountingSubCategory("Financial Fees", AccountingCategory.FEES))
