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
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingViewModel
import com.github.se.assocify.ui.screens.treasury.accounting.balance.balanceDetailedGraph
import com.github.se.assocify.ui.screens.treasury.accounting.budget.budgetDetailedGraph
import com.github.se.assocify.ui.screens.treasury.accounting.newcategory.addAccountingCategory
import com.github.se.assocify.ui.screens.treasury.receiptstab.ReceiptListViewModel
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
    val receiptListViewModel = remember { ReceiptListViewModel(navigationActions, receiptsAPI) }
    val accountingViewModel = remember {
      AccountingViewModel(accountingCategoryAPI, accountingSubCategoryAPI)
    }
    val treasuryViewModel = remember {
      TreasuryViewModel(navigationActions, receiptListViewModel, accountingViewModel)
    }
    TreasuryScreen(navigationActions, accountingViewModel, receiptListViewModel, treasuryViewModel)
  }
  receiptGraph(navigationActions, receiptsAPI)
  budgetDetailedGraph(
      navigationActions, budgetAPI, balanceAPI, accountingSubCategoryAPI, accountingCategoryAPI)
  balanceDetailedGraph(
      navigationActions, budgetAPI, balanceAPI, accountingSubCategoryAPI, accountingCategoryAPI)
  addAccountingCategory(navigationActions)
}
