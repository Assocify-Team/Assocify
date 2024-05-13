package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.compose.runtime.Composable
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingPage
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingScreen
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingViewModel

/**
 * The accounting screen displaying the budget screen of the association
 *
 * @param navigationActions: The navigation actions
 * @param budgetViewModel: The view model for the budget screen
 */
@Composable
fun BudgetScreen(navigationActions: NavigationActions, budgetViewModel: AccountingViewModel) {
  AccountingScreen(AccountingPage.BUDGET, navigationActions, budgetViewModel)
}
