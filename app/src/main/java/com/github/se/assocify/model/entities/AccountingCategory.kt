package com.github.se.assocify.model.entities

/**
 * Represents the category of an accounting item.
 *
 * @param uid unique identifier of the category
 * @param name name of the category (ex: Pole)
 */
data class AccountingCategory(val uid: String, val name: String) {}
