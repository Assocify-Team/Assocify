package com.github.se.assocify.ui.screens.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.homeGraph(navigationActions: NavigationActions) {
    composable(
        route = Destination.Home.route,
    ) {
        HomeScreen(navigationActions)
    }
}