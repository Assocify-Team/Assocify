package com.github.se.assocify.ui.screens.profile

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.profile.events.profileEventsGraph
import com.github.se.assocify.ui.screens.profile.members.profileMembersGraph
import com.github.se.assocify.ui.screens.profile.preferences.profilePreferencesGraph
import com.github.se.assocify.ui.screens.profile.treasury.profileTreasuryGraph

fun NavGraphBuilder.profileGraph(
    navigationActions: NavigationActions,
    userAPI: UserAPI,
    associationAPI: AssociationAPI
) {
  composable(
      route = Destination.Profile.route,
  ) {
    val profileViewModel = remember { ProfileViewModel(associationAPI, userAPI, navigationActions) }
    ProfileScreen(navigationActions, profileViewModel)
  }

  //  profileNotificationsGraph(navigationActions)
  //  profileSecurityPrivacyGraph(navigationActions)
  profilePreferencesGraph(navigationActions)
  //  profileRolesGraph(navigationActions)
  profileMembersGraph(navigationActions)
  profileTreasuryGraph(navigationActions)
  profileEventsGraph(navigationActions)
}
