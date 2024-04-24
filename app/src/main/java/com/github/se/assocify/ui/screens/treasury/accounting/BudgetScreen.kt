package com.github.se.assocify.ui.screens.treasury.accounting

import androidx.compose.runtime.Composable

/** The accounting screen displaying the budget screen of the association */
@Composable
fun Budget() {
  // TODO: fetch all these list from viewmodel
  val categoryList = listOf("Global", "Category", "Commissions", "Events", "Projects", "Other")
  // TODO: change this when budget entity and api are implemented
  val budgetLines =
      listOf(
          "Logistic Category" to "1000",
          "Communication Category" to "2000",
          "Game*" to "3000",
          "ICBD" to "4000",
          "Balelec" to "5000",
      )

  val categoryMapping =
      mapOf(
          "Global" to
              listOf("Logistic Category", "Communication Category", "Game*", "ICBD", "Balelec"),
          "Category" to listOf("Logistic Category", "Communication Category"),
          "Commissions" to listOf("Game*"),
          "Events" to listOf("ICBD", "Balelec"),
          "Projects" to listOf(),
          "Other" to listOf())
  Accounting("budget", categoryList, budgetLines, categoryMapping)
}
