package com.github.se.assocify.ui.screens.treasury.accounting.balance

import androidx.compose.runtime.Composable
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingPage
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingScreen
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingViewModel

/**
 * The accounting screen displaying the balance screen of the association
 *
 * @param navigationActions: The navigation actions
 * @param accountingViewModel: The view model for the accounting screen
 */
@Composable
fun BalanceScreen(navigationActions: NavigationActions, accountingViewModel: AccountingViewModel) {
  AccountingScreen(
      page = AccountingPage.BALANCE,
      navigationActions = navigationActions,
      accountingViewModel = accountingViewModel)
}
