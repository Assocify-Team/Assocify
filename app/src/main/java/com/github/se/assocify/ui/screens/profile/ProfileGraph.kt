package com.github.se.assocify.ui.screens.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.profile.notifications.profileNotificationsGraph
import com.github.se.assocify.ui.screens.profile.security_privacy.profileSecurityPrivacyGraph
import com.github.se.assocify.ui.screens.profile.theme.profileThemeGraph

fun NavGraphBuilder.profileGraph(
    navigationActions: NavigationActions,
    userAPI: UserAPI,
    associationAPI: AssociationAPI
) {
  composable(
      route = Destination.Profile.route,
  ) {
    ProfileScreen(navigationActions, ProfileViewModel(associationAPI, userAPI))
  }

  profileNotificationsGraph(navigationActions)
  profileSecurityPrivacyGraph(navigationActions)
  profileThemeGraph(navigationActions)
}
