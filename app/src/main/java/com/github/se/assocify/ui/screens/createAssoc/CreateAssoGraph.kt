package com.github.se.assocify.ui.screens.createAssoc

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.createAsso.CreateAssoScreen

fun NavGraphBuilder.createAssoGraph(
    navigationActions: NavigationActions,
    userAPI: UserAPI,
    associationAPI: AssociationAPI
) {
  composable(route = Destination.CreateAsso.route) { CreateAssoScreen(navigationActions) }
}
