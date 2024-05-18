package com.github.se.assocify.ui.screens.treasury.accounting.balance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.CenteredCircularIndicator
import com.github.se.assocify.ui.composables.ErrorMessagePage
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
  val balanceState by balanceDetailedViewModel.uiState.collectAsState()

  if (balanceState.loading) {
    CenteredCircularIndicator()
    return
  }

  if (balanceState.error != null) {
    ErrorMessagePage(
        errorMessage = balanceState.error,
        onBack = { navigationActions.back() },
        title =
            if (balanceState.subCategory != null) balanceState.subCategory!!.name else "Error") {
          balanceDetailedViewModel.loadBalanceDetails()
        }
    return
  }

  AccountingDetailedScreen(
      page = AccountingPage.BALANCE,
      navigationActions = navigationActions,
      budgetDetailedViewModel = budgetDetailedViewModel,
      balanceDetailedViewModel = balanceDetailedViewModel)
}
