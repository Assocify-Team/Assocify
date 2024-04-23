package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.compose.runtime.Composable
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.Accounting
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingPage

/** The accounting screen displaying the budget screen of the association */
@Composable
fun Budget(navigationActions: NavigationActions) {
  // TODO: fetch from db
  val list =
      listOf(
          AccountingSubCategory("1", "Administration Pole", AccountingCategory("Pole"), 2000),
          AccountingSubCategory("2", "Presidency Pole", AccountingCategory("Pole"), -400),
          AccountingSubCategory("3", "Balelec", AccountingCategory("Events"), 1000),
          AccountingSubCategory("4", "Champachelor", AccountingCategory("Events"), 5000),
          AccountingSubCategory("5", "OGJ", AccountingCategory("Commission"), 6000),
          AccountingSubCategory("6", "Communication Fees", AccountingCategory("Fees"), 3000))
  Accounting(AccountingPage.BUDGET, list, navigationActions)
}
