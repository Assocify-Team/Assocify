package com.github.se.assocify.ui.screens.treasury.receiptstab.receipt

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.ReceiptAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.receiptGraph(navigationActions: NavigationActions, receiptAPI: ReceiptAPI) {
  composable(route = Destination.NewReceipt.route) {
    val newReceiptViewModel = remember { ReceiptViewModel(navigationActions, receiptAPI) }
    ReceiptScreen(newReceiptViewModel)
  }
  composable(Destination.EditReceipt("{receiptUid}").route) { backStackEntry ->
    backStackEntry.arguments?.getString("receiptUid")?.let {
      val receiptViewModel = remember {
        ReceiptViewModel(receiptUid = it, navActions = navigationActions, receiptAPI)
      }
      ReceiptScreen(receiptViewModel)
    }
  }
}
