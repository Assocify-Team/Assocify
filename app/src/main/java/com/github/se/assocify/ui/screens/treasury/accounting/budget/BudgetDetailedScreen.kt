package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.compose.runtime.Composable
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingDetailedScreen
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingPage
import com.github.se.assocify.ui.screens.treasury.accounting.balance.BalanceDetailedViewModel

/**
 * Detailed budget screen for a subcategory
 *
 * @param subCategoryUid the subcategory uid
 * @param navigationActions the navigation actions
 * @param budgetDetailedViewModel the view model for the budget detailed screen
 */
@Composable
fun BudgetDetailedScreen(
    subCategoryUid: String,
    navigationActions: NavigationActions,
    budgetDetailedViewModel: BudgetDetailedViewModel,
    balanceDetailedViewModel: BalanceDetailedViewModel
) {
  AccountingDetailedScreen(
      AccountingPage.BUDGET,
      subCategoryUid,
      navigationActions,
      budgetDetailedViewModel,
      balanceDetailedViewModel)
}
