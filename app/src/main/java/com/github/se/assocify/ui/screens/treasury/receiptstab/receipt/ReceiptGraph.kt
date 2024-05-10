package com.github.se.assocify.ui.screens.treasury.receiptstab.receipt

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.receiptGraph(navigationActions: NavigationActions) {
  composable(route = Destination.NewReceipt.route) {
    ReceiptScreen(ReceiptViewModel(navigationActions))
  }
  composable(Destination.EditReceipt("{receiptUid}").route) { backStackEntry ->
    backStackEntry.arguments?.getString("receiptUid")?.let {
      ReceiptScreen(ReceiptViewModel(receiptUid = it, navActions = navigationActions))
    }
  }
}
