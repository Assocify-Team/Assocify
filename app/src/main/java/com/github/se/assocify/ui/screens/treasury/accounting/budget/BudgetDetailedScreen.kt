package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.CenteredCircularIndicator
import com.github.se.assocify.ui.composables.ErrorMessagePage
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingDetailedScreen
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingPage
import com.github.se.assocify.ui.screens.treasury.accounting.balance.BalanceDetailedViewModel

/**
 * Detailed budget screen for a subcategory
 *
 * @param navigationActions the navigation actions
 * @param budgetDetailedViewModel the view model for the budget detailed screen
 * @param balanceDetailedViewModel the view model for the balance detailed screen
 */
@Composable
fun BudgetDetailedScreen(
    navigationActions: NavigationActions,
    budgetDetailedViewModel: BudgetDetailedViewModel,
    balanceDetailedViewModel: BalanceDetailedViewModel
) {
  val budgetState by budgetDetailedViewModel.uiState.collectAsState()

  if (budgetState.loading) {
    CenteredCircularIndicator()
    return
  }

  if (budgetState.error != null) {
    ErrorMessagePage(
        errorMessage = budgetState.error,
        onBack = { navigationActions.back() },
        title = if (budgetState.subCategory != null) budgetState.subCategory!!.name else "Error") {
          budgetDetailedViewModel.loadBudgetDetails()
        }
    return
  }

    val subCategory = budgetState.subCategory
    val snackbarState = budgetState.snackbarState

  AccountingDetailedScreen(
      AccountingPage.BUDGET, navigationActions, subCategory, snackbarState, budgetDetailedViewModel, balanceDetailedViewModel)
}
