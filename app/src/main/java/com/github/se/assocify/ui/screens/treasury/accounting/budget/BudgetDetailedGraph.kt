package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.BalanceAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.balance.BalanceDetailedViewModel

fun NavGraphBuilder.budgetDetailedGraph(
    navigationActions: NavigationActions,
    budgetAPI: BudgetAPI,
    balanceAPI: BalanceAPI,
    accountingSubCategoryAPI: AccountingSubCategoryAPI,
    accountingCategoryAPI: AccountingCategoryAPI
) {
  composable(Destination.BudgetDetailed("{subCategoryUid}").route) { backStackEntry ->
    backStackEntry.arguments?.getString("subCategoryUid")?.let {
      val budgetDetailedViewModel = remember {
        BudgetDetailedViewModel(budgetAPI, accountingSubCategoryAPI, accountingCategoryAPI, it)
      }
      val balanceDetailedViewModel = remember {
        BalanceDetailedViewModel(balanceAPI, accountingSubCategoryAPI, accountingCategoryAPI, it)
      }
      BudgetDetailedScreen(navigationActions, budgetDetailedViewModel, balanceDetailedViewModel)
    }
  }
}
