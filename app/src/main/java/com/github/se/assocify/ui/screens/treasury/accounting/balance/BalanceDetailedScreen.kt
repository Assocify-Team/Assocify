package com.github.se.assocify.ui.screens.treasury.accounting.balance

import androidx.compose.runtime.Composable
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingDetailedScreen
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingPage
import com.github.se.assocify.ui.screens.treasury.accounting.budget.BudgetDetailedViewModel

/**
 * Detailed balance screen for a subcategory
 *
 * @param subCategoryUid the subcategory uid
 * @param navigationActions the navigation actions
 * @param budgetAPI the budget api
 */
@Composable
fun BalanceDetailedScreen(
    subCategoryUid: String,
    navigationActions: NavigationActions,
    budgetDetailedViewModel: BudgetDetailedViewModel
) {
  AccountingDetailedScreen(
      page = AccountingPage.BALANCE,
      navigationActions = navigationActions,
      budgetDetailedViewModel = budgetDetailedViewModel)
}
