package com.github.se.assocify.ui.screens.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.createAsso.CreateAssoScreen
import com.github.se.assocify.ui.screens.selectAssoc.SelectAssociation

fun NavGraphBuilder.loginGraph(navigationActions: NavigationActions) {
    navigation(startDestination = Destination.Login.route, route = "login"){
        composable(route = Destination.Login.Authentication.route) {
            LoginScreen(navigationActions)
        }
        composable(route = Destination.Login.SelectAsso.route) {
            SelectAssociation(registeredAssociation = emptyList())
        }
        composable(route = Destination.Login.CreateAsso.route) {
            CreateAssoScreen()
        }
    }
}
