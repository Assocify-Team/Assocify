package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.compose.runtime.Composable
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.ui.screens.treasury.accounting.Accounting

/** The accounting screen displaying the budget screen of the association */
@Composable
fun Budget() {
  //TODO: fetch from db
  val list =
    listOf(
      AccountingSubCategory("Administration Pole", AccountingCategory("Pole"), 2000),
      AccountingSubCategory("Presidency Pole", AccountingCategory("Pole"), -400),
      AccountingSubCategory("Balelec", AccountingCategory("Event"), 1000),
      AccountingSubCategory("Champachelor", AccountingCategory("Event"), 5000),
      AccountingSubCategory("OGJ", AccountingCategory("Commission"), 6000),
      AccountingSubCategory("Communication Fees", AccountingCategory("Fees"), 3000)
    )
  Accounting("budget", list)
}
