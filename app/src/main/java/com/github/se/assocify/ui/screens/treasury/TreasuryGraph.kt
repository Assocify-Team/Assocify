package com.github.se.assocify.ui.screens.treasury

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.treasuryGraph(navigationActions: NavigationActions, currentUser: CurrentUser) {
  composable(
      route = Destination.Treasury.route,
  ) {
    TreasuryScreen(navigationActions, currentUser)
  }
}
