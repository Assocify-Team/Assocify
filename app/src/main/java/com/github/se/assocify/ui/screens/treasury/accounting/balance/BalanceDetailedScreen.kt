package com.github.se.assocify.ui.screens.treasury.accounting.balance

import androidx.compose.runtime.Composable
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingDetailedScreen
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingPage
import com.github.se.assocify.ui.screens.treasury.accounting.budget.BudgetDetailedViewModel

/**
 * Detailed balance screen for a subcategory
 *
 * @param navigationActions the navigation actions
 * @param budgetDetailedViewModel the view model for the budget detailed screen
 * @param balanceDetailedViewModel the view model for the balance detailed screen
 */
@Composable
fun BalanceDetailedScreen(
    navigationActions: NavigationActions,
    budgetDetailedViewModel: BudgetDetailedViewModel,
    balanceDetailedViewModel: BalanceDetailedViewModel
) {
  AccountingDetailedScreen(
      page = AccountingPage.BALANCE,
      navigationActions = navigationActions,
      budgetDetailedViewModel = budgetDetailedViewModel,
      balanceDetailedViewModel = balanceDetailedViewModel)
}
