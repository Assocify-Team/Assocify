package com.github.se.assocify.ui.screens.treasury

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.treasuryGraph(navigationActions: NavigationActions) {
    composable(
        route = Destination.Treasury.route,
    ) {
        TreasuryScreen(navigationActions)
    }
}