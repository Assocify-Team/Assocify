package com.github.se.assocify.ui.screens.createAssociation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.createAssociationGraph(
    navigationActions: NavigationActions,
    userAPI: UserAPI,
    associationAPI: AssociationAPI
) {
  composable(route = Destination.CreateAsso.route) {
    CreateAssociationScreen(
        navigationActions, CreateAssociationViewmodel(associationAPI, userAPI, navigationActions))
  }
}
