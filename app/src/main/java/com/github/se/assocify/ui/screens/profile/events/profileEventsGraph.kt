package com.github.se.assocify.ui.screens.profile.events

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.profileEventsGraph(navigationActions: NavigationActions, eventAPI: EventAPI) {
  composable(route = Destination.ProfileEvents.route) {
    ProfileEventsScreen(
        navigationActions,
        profileEventsViewModel = ProfileEventsViewModel(eventAPI, navigationActions))
  }
}
