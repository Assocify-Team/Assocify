package com.github.se.assocify.ui.screens.selectAssoc

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.selectAssoc.SelectAssociation

fun NavGraphBuilder.selectAssoGraph(
    navigationActions: NavigationActions,
    userAPI: UserAPI,
    associationAPI: AssociationAPI
) {
  composable(route = Destination.SelectAsso.route) {
    SelectAssociation(
        registeredAssociation = emptyList(),
        navigationActions = navigationActions,
        associationAPI = associationAPI)
  }
}
