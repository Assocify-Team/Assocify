package com.github.se.assocify.model.entities

data class BudgetItem (
    val uid: String,
    val nameItem: String,
    val description: String,
    val amount: Int,
    val category: AccountingCategory
)

