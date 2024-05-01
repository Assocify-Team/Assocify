package com.github.se.assocify.ui.screens.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.loginGraph(
    navigationActions: NavigationActions,
    userAPI: UserAPI,
) {
  composable(route = Destination.Login.route) {
    LoginScreen(navigationActions, userAPI)
  }
}
