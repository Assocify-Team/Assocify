package com.github.se.assocify.model.entities

data class BalanceItem(
    val uid: String,
    val nameItem: String,
    val amount: Int,
    val description: String,
    val category: AccountingSubCategory, //Game*
    val date: String,
    val receipt: Receipt?,
    val assignee: User?, // User who is assigned to pay this item or to receive it
    val type: FlowType,
    val status: Phase
)

enum class FlowType {
    EARNING,
    EXPENSE
}
