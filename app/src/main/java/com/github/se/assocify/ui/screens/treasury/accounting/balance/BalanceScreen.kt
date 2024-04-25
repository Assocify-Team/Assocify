package com.github.se.assocify.ui.screens.treasury.accounting.balance

import androidx.compose.runtime.Composable
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.Accounting
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingPage

/** The accounting screen displaying the balance screen of the association */
@Composable
fun Balance(navigationActions: NavigationActions) {
  // TODO: fetch from db
  val subCategoryList =
      listOf(
          AccountingSubCategory("7", "Logistic Pole", AccountingCategory("Pole"), 1000),
          AccountingSubCategory("8", "Communication Pole", AccountingCategory("Pole"), -500),
          AccountingSubCategory("9", "ICBD", AccountingCategory("Events"), 2000),
          AccountingSubCategory("10", "SDF", AccountingCategory("Events"), 10000),
          AccountingSubCategory("11", "Game*", AccountingCategory("Commission"), 5000),
          AccountingSubCategory("12", "Financial Fees", AccountingCategory("Fees"), 6000))
  Accounting(AccountingPage.BALANCE, subCategoryList, navigationActions)
}
