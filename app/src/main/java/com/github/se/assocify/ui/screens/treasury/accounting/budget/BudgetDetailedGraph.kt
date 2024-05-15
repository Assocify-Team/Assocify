package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.BalanceAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.database.ReceiptAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.balance.BalanceDetailedViewModel

fun NavGraphBuilder.budgetDetailedGraph(
    navigationActions: NavigationActions,
    budgetAPI: BudgetAPI,
    balanceAPI: BalanceAPI,
    receiptAPI: ReceiptAPI,
    subCategoryAPI: AccountingSubCategoryAPI
) {
  composable(Destination.BudgetDetailed("{subCategoryUid}").route) { backStackEntry ->
    backStackEntry.arguments?.getString("subCategoryUid")?.let {
      BudgetDetailedScreen(
          subCategoryUid = it,
          navigationActions,
          BudgetDetailedViewModel(budgetAPI, it),
          BalanceDetailedViewModel(balanceAPI, receiptAPI, subCategoryAPI,it))
    }
  }
}
