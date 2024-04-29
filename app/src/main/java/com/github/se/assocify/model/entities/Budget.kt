package com.github.se.assocify.model.entities

/**
 * Represents a budget sheet
 */
data class Budget(val budget: List<BudgetItem>, val name: String, val uid: String)
