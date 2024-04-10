package com.github.se.assocify.ui.screens.selectAssoc

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.home.HomeScreen

fun NavGraphBuilder.selectAssocGraph(navigationActions: NavigationActions) {
    composable(
        route = Destination.SelectAsso.route,
    ) {
        SelectAssociation(registeredAssociation = emptyList())
    }
}