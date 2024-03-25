package com.github.se.assocify.ui.screens.event

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.eventGraph(navigationActions: NavigationActions) {
    composable(
        route = Destination.Event.route,
    ) {
        EventScreen(navigationActions)
    }
}