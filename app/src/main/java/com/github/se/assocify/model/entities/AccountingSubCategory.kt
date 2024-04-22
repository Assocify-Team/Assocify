package com.github.se.assocify.model.entities

/**
 * Represents the subcategory of an accounting item which is related to a category.
 */
data class AccountingSubCategory(val name: String, val category: AccountingCategory) {
    constructor() : this("", AccountingCategory.OTHER)
}