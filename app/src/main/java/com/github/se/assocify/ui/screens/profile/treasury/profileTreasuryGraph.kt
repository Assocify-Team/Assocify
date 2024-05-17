package com.github.se.assocify.ui.screens.profile.treasury

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.profileTreasuryGraph(navigationActions: NavigationActions) {
  composable(route = Destination.ProfileTreasury.route) { ProfileTreasuryScreen(navigationActions) }
}
