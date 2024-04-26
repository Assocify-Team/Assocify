package com.github.se.assocify.ui.screens.profile.theme


import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.profile.security_privacy.ProfileSecurity_PrivacyScreen

fun NavGraphBuilder.profileThemeGraph(navigationActions: NavigationActions) {
    composable(route = Destination.ProfileTheme.route) { ProfileThemeScreen(navActions = navigationActions) }
}