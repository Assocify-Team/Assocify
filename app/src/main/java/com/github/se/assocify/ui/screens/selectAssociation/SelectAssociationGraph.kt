package com.github.se.assocify.ui.screens.selectAssociation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.localsave.LoginSave
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.selectAssociationGraph(
    navigationActions: NavigationActions,
    userAPI: UserAPI,
    associationAPI: AssociationAPI,
    loginSaver: LoginSave
) {
  composable(route = Destination.SelectAsso.route) {
    SelectAssociation(
        navActions = navigationActions,
        associationAPI = associationAPI,
        userAPI = userAPI,
        loginSaver = loginSaver
    )
  }
}
