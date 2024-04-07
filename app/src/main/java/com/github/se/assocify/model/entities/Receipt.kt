package com.github.se.assocify.model.entities

import java.time.LocalDate

data class Receipt(
    val uid: String,
    val title: String,
    val description: String,
    val amount: Double,
    val payer: String, // User uid
    val date: LocalDate?,
    val incoming: Boolean
) {
  constructor() : this("", "", "", 0.0, "", null, false)
}
