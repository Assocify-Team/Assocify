package com.github.se.assocify.ui.screens.treasury.accounting.balance

import androidx.compose.runtime.Composable
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.ui.screens.treasury.accounting.Accounting

/** The accounting screen displaying the balance screen of the association */
@Composable
fun Balance() {
  // TODO: fetch from db
  val subCategoryList =
      listOf(
          AccountingSubCategory("Logistic Pole", AccountingCategory("Pole"), 1000),
          AccountingSubCategory("Communication Pole", AccountingCategory("Pole"), -500),
          AccountingSubCategory("ICBD", AccountingCategory("Events"), 2000),
          AccountingSubCategory("SDF", AccountingCategory("Events"), 10000),
          AccountingSubCategory("Game*", AccountingCategory("Commission"), 5000),
          AccountingSubCategory("Financial Fees", AccountingCategory("Fees"), 6000))
  Accounting("balance", subCategoryList)
}
