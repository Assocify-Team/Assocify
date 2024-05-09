package com.github.se.assocify.ui.screens.profile.preferences

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.profilePreferencesGraph(navigationActions: NavigationActions) {
  composable(route = Destination.ProfilePreferences.route) {
    ProfilePreferencesScreen(navActions = navigationActions)
  }
}