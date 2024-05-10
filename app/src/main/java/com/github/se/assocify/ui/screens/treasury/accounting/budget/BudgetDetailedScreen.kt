package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.compose.runtime.Composable
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingDetailedScreen
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingPage

/**
 * Detailed budget screen for a subcategory
 *
 * @param subCategoryUid the subcategory uid
 * @param navigationActions the navigation actions
 * @param budgetAPI the budget api
 * @param budgetDetailedViewModel the view model for the budget detailed screen
 */
@Composable
fun BudgetDetailedScreen(
    subCategoryUid: String,
    navigationActions: NavigationActions,
    budgetDetailedViewModel: BudgetDetailedViewModel
) {
  AccountingDetailedScreen(
      AccountingPage.BUDGET, subCategoryUid, navigationActions, budgetDetailedViewModel)
}
