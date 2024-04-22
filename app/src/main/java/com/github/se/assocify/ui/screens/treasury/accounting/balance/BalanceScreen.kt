package com.github.se.assocify.ui.screens.treasury.accounting.balance

import androidx.compose.runtime.Composable
import com.github.se.assocify.ui.screens.treasury.accounting.Accounting

/** The accounting screen displaying the balance screen of the association */
@Composable
fun Balance() {
  // TODO: fetch all these list from viewmodel

  val categoryList = listOf("Global", "Category", "Commissions", "Events")
  // TODO: change this when budget entity and api are implemented
  val lines =
      listOf(
          "Administration Category" to "1000",
          "Presidence Category" to "2000",
          "OGJ" to "3000",
          "SDF" to "4000",
          "Champachelor" to "5000",
      )

  val mapping =
      mapOf(
          "Global" to
              listOf(
                  "Administration Category", "Presidence Category", "OGJ", "SDF", "Champachelor"),
          "Category" to listOf("Administration Category", "Presidence Category"),
          "Commissions" to listOf("OGJ"),
          "Events" to listOf("SDF", "Champachelor"))

  Accounting("balance", categoryList, lines, mapping)
}
