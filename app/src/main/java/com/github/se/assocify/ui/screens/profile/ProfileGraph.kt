package com.github.se.assocify.ui.screens.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.profile.members.profileMembersGraph
import com.github.se.assocify.ui.screens.profile.notifications.profileNotificationsGraph
import com.github.se.assocify.ui.screens.profile.preferences.profilePreferencesGraph
import com.github.se.assocify.ui.screens.profile.roles.profileRolesGraph
import com.github.se.assocify.ui.screens.profile.securityPrivacy.profileSecurityPrivacyGraph

fun NavGraphBuilder.profileGraph(
    navigationActions: NavigationActions,
    userAPI: UserAPI,
    associationAPI: AssociationAPI
) {
  composable(
      route = Destination.Profile.route,
  ) {
    ProfileScreen(navigationActions, ProfileViewModel(associationAPI, userAPI, navigationActions))
  }

  profileNotificationsGraph(navigationActions)
  profileSecurityPrivacyGraph(navigationActions)
  profilePreferencesGraph(navigationActions)
  profileRolesGraph(navigationActions)
  profileMembersGraph(navigationActions)
}
