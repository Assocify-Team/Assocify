package com.github.se.assocify.ui.screens.treasury

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.BalanceAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.database.ReceiptAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.balance.balanceDetailedGraph
import com.github.se.assocify.ui.screens.treasury.accounting.budget.budgetDetailedGraph
import com.github.se.assocify.ui.screens.treasury.receiptstab.receipt.receiptGraph

fun NavGraphBuilder.treasuryGraph(
    navigationActions: NavigationActions,
    budgetAPI: BudgetAPI,
    balanceAPI: BalanceAPI,
    receiptsAPI: ReceiptAPI,
    accountingCategoryAPI: AccountingCategoryAPI,
    accountingSubCategoryAPI: AccountingSubCategoryAPI
) {

  composable(
      route = Destination.Treasury.route,
  ) {
    val treasuryViewModel = remember {
      TreasuryViewModel(navigationActions, receiptsAPI, accountingCategoryAPI, accountingSubCategoryAPI, balanceAPI, budgetAPI)
    }
    TreasuryScreen(navigationActions, treasuryViewModel)
  }
  receiptGraph(navigationActions, receiptsAPI)
  budgetDetailedGraph(
      navigationActions,
      budgetAPI,
      balanceAPI,
      receiptsAPI,
      accountingSubCategoryAPI,
      accountingCategoryAPI)
  balanceDetailedGraph(
      navigationActions,
      budgetAPI,
      balanceAPI,
      receiptsAPI,
      accountingSubCategoryAPI,
      accountingCategoryAPI)
}
