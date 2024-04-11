package com.github.se.assocify.ui.screens.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.loginGraph(
    navigationActions: NavigationActions,
    userAPI: UserAPI,
    associationAPI: AssociationAPI
) {
  // navigation(startDestination = Destination.Login.route, route = "loginGraph") {
  composable(route = Destination.Login.route) { LoginScreen(navigationActions, userAPI) }
  /* composable(route = Destination.Login.SelectAsso.route) {
      SelectAssociation(
          registeredAssociation = emptyList(),
          navigationActions = navigationActions,
          associationAPI = associationAPI)
    }
    composable(route = Destination.Login.CreateAsso.route) { CreateAssoScreen() }
  }*/
}
