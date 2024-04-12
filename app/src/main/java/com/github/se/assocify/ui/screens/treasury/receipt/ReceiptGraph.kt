package com.github.se.assocify.ui.screens.treasury.receipt

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.receiptGraph(
    navigationActions: NavigationActions,
    userAPI: UserAPI,
    associationAPI: AssociationAPI
) {
  composable(route = Destination.Receipt.route) { ReceiptScreen(navigationActions) }
}
