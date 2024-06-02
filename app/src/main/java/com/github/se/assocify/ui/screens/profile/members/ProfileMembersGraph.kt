package com.github.se.assocify.ui.screens.profile.members

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.profileMembersGraph(
    navigationActions: NavigationActions,
    associationAPI: AssociationAPI,
    userAPI: UserAPI
) {
  composable(route = Destination.ProfileMembers.route) {
    val viewModel = remember { ProfileMembersViewModel(associationAPI, userAPI) }
    ProfileMembersScreen(navigationActions, viewModel)
  }
}
