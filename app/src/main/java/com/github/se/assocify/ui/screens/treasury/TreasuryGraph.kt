package com.github.se.assocify.ui.screens.treasury

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.balance.balanceDetailedGraph
import com.github.se.assocify.ui.screens.treasury.accounting.budget.budgetDetailedGraph
import com.github.se.assocify.ui.screens.treasury.accounting.newcategory.addAccountingCategory
import com.github.se.assocify.ui.screens.treasury.receiptstab.ReceiptListViewModel
import com.github.se.assocify.ui.screens.treasury.receiptstab.receipt.receiptGraph

fun NavGraphBuilder.treasuryGraph(navigationActions: NavigationActions, budgetAPI: BudgetAPI) {

  composable(
      route = Destination.Treasury.route,
  ) {
    val receiptListViewModel = remember { ReceiptListViewModel(navigationActions) }
    val treasuryViewModel = remember { TreasuryViewModel(navigationActions, receiptListViewModel) }
    TreasuryScreen(navigationActions, receiptListViewModel, treasuryViewModel)
  }
  receiptGraph(navigationActions)
  budgetDetailedGraph(navigationActions, budgetAPI)
  balanceDetailedGraph(navigationActions, budgetAPI)
  addAccountingCategory(navigationActions)
}
