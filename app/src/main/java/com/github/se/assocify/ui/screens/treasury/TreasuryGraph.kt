package com.github.se.assocify.ui.screens.treasury

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.BalanceAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.balance.balanceDetailedGraph
import com.github.se.assocify.ui.screens.treasury.accounting.budget.BudgetViewModel
import com.github.se.assocify.ui.screens.treasury.accounting.budget.budgetDetailedGraph
import com.github.se.assocify.ui.screens.treasury.accounting.newcategory.addAccountingCategory
import com.github.se.assocify.ui.screens.treasury.receiptstab.ReceiptListViewModel
import com.github.se.assocify.ui.screens.treasury.receiptstab.receipt.receiptGraph

fun NavGraphBuilder.treasuryGraph(
    navigationActions: NavigationActions,
    budgetAPI: BudgetAPI,
    balanceAPI: BalanceAPI,
    accountingCategoriesAPI: AccountingCategoryAPI,
    accountingSubCategoryAPI: AccountingSubCategoryAPI
) {
  composable(
      route = Destination.Treasury.route,
  ) {
    val receiptListViewModel = ReceiptListViewModel(navigationActions)
    val budgetViewModel = BudgetViewModel(accountingCategoriesAPI, accountingSubCategoryAPI)
    TreasuryScreen(
        navigationActions,
        budgetViewModel,
        receiptListViewModel,
        TreasuryViewModel(
            navActions = navigationActions, receiptListViewModel = receiptListViewModel))
  }
  receiptGraph(navigationActions)
  budgetDetailedGraph(navigationActions, budgetAPI, balanceAPI)
  balanceDetailedGraph(navigationActions, budgetAPI, balanceAPI)
  addAccountingCategory(navigationActions)
}
