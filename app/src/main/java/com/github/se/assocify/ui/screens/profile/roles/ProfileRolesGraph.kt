package com.github.se.assocify.ui.screens.profile.roles

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.profile.notifications.ProfileNotificationsScreen

fun NavGraphBuilder.profileRolesGraph(navigationActions: NavigationActions) {
    composable(route = Destination.ProfileRoles.route) {
        ProfileRolesScreen(navigationActions)
    }
}
