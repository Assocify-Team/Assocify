package com.github.se.assocify.model.entities

data class Receipt(
    val uid: String,
    val title: String,
    val description: String,
    val amount: Double,
    val payer: User,
    val date: String,
    val incoming: Boolean
) {
    constructor() : this("", "", "", 0.0, User("", "", Role("")), "", false)

}