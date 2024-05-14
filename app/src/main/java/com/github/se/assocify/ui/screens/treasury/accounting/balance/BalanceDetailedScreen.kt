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
 */
@Composable
fun BalanceDetailedScreen(
    subCategoryUid: String,
    navigationActions: NavigationActions,
    budgetDetailedViewModel: BudgetDetailedViewModel,
    balanceDetailedViewModel: BalanceDetailedViewModel
) {
  AccountingDetailedScreen(
      page = AccountingPage.BALANCE,
      subCategoryUid = subCategoryUid,
      navigationActions = navigationActions,
      budgetDetailedViewModel = budgetDetailedViewModel,
      balanceDetailedViewModel = balanceDetailedViewModel)
}
