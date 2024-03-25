package com.github.se.assocify.ui.screens.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.profileGraph(navigationActions: NavigationActions) {
    composable(
        route = Destination.Profile.route,
    ) {
        ProfileScreen(navigationActions)
    }
}