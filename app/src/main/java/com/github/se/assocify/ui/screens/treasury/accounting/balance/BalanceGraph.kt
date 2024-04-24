package com.github.se.assocify.ui.screens.treasury.accounting.balance

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.balanceDetailedGraph(navigationActions: NavigationActions) {
  composable(Destination.BalanceDetailed("{subCategoryUid}").route) { backStackEntry ->
    backStackEntry.arguments?.getString("subCategoryUid")?.let {
      BalanceDetailedScreen(subCategoryUid = it, navigationActions)
    }
  }
}
