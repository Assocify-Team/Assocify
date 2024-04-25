package com.github.se.assocify.ui.screens.treasury

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.balance.balanceDetailedGraph
import com.github.se.assocify.ui.screens.treasury.accounting.budget.budgetDetailedGraph
import com.github.se.assocify.ui.screens.treasury.receipt.receiptGraph

fun NavGraphBuilder.treasuryGraph(navigationActions: NavigationActions) {
  composable(
      route = Destination.Treasury.route,
  ) {
    TreasuryScreen(navigationActions)
  }
  receiptGraph(navigationActions)
  budgetDetailedGraph(navigationActions)
  balanceDetailedGraph(navigationActions)
}
