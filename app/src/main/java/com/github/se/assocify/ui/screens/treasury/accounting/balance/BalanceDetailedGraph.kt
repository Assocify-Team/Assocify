package com.github.se.assocify.ui.screens.treasury.accounting.balance

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.BalanceAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.budget.BudgetDetailedViewModel

fun NavGraphBuilder.balanceDetailedGraph(
    navigationActions: NavigationActions,
    budgetAPI: BudgetAPI,
    balanceAPI: BalanceAPI,
    accountingSubCategoryAPI: AccountingSubCategoryAPI,
    accountingCategoryAPI: AccountingCategoryAPI
) {
  composable(Destination.BalanceDetailed("{subCategoryUid}").route) { backStackEntry ->
    backStackEntry.arguments?.getString("subCategoryUid")?.let {
      val budgetDetailedViewModel =
          BudgetDetailedViewModel(budgetAPI, accountingSubCategoryAPI, accountingCategoryAPI, it)
      val balanceDetailedViewModel =
          BalanceDetailedViewModel(balanceAPI, accountingSubCategoryAPI, accountingCategoryAPI, it)
      BalanceDetailedScreen(navigationActions, budgetDetailedViewModel, balanceDetailedViewModel)
    }
  }
}
