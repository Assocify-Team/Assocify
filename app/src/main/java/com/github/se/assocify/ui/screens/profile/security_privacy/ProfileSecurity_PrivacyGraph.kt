package com.github.se.assocify.ui.screens.profile.security_privacy

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.profileSecurityPrivacyGraph(navigationActions: NavigationActions) {
  composable(route = Destination.ProfileSecurityPrivacy.route) {
    ProfileSecurity_PrivacyScreen(navActions = navigationActions)
  }
}
