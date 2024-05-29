package com.github.se.assocify.ui.screens.profile.members

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.profileMembersGraph(
    navigationActions: NavigationActions,
    associationAPI: AssociationAPI
) {
  composable(route = Destination.ProfileMembers.route) {
    ProfileMembersScreen(
        navigationActions, ProfileMembersViewModel(navigationActions, associationAPI))
  }
}
