package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.budgetDetailedGraph(
    navigationActions: NavigationActions,
    budgetAPI: BudgetAPI,
    accountingSubCategoryAPI: AccountingSubCategoryAPI
) {
  composable(Destination.BudgetDetailed("{subCategoryUid}").route) { backStackEntry ->
    backStackEntry.arguments?.getString("subCategoryUid")?.let {
      val budgetViewModel = BudgetDetailedViewModel(budgetAPI, accountingSubCategoryAPI, it)
      BudgetDetailedScreen(subCategoryUid = it, navigationActions, budgetViewModel)
    }
  }
}
