package com.github.se.assocify.ui.screens.createAsso

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.selectAssoc.SelectAssociation

fun NavGraphBuilder.createAssocGraph(navigationActions: NavigationActions) {
    composable(
        route = Destination.CreateAsso.route,
    ) {
        CreateAssoScreen()
    }
}