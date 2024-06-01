package com.github.se.assocify.ui.screens.profile.preferences

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.localsave.LoginSave
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.theme.ThemeViewModel

fun NavGraphBuilder.profilePreferencesGraph(navigationActions: NavigationActions, appThemeVM: ThemeViewModel, localSave: LoginSave) {
  composable(route = Destination.ProfilePreferences.route) {
    ProfilePreferencesScreen(navActions = navigationActions, appThemeViewModel = appThemeVM, localSave = localSave)
  }
}
