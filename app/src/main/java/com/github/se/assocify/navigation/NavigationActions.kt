package com.github.se.assocify.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

class NavigationActions(private val navController: NavHostController) {
  fun navigateToMainTab(destination: Destination) {
    if (destination in MAIN_TABS_LIST) {
      navController.navigate(destination.route) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
      }
    } else {
      throw IllegalArgumentException("Destination $destination is not a main tab")
    }
  }

  fun navigateTo(destination: Destination) {
    navController.navigate(destination.route)
  }

  fun onLogin(userHasMembership: Boolean) {
    if (userHasMembership) {
      navigateTo(Destination.Home)
    } else {
      navigateTo(Destination.SelectAsso)
    }
  }

  fun onAuthError() {
    // throw Exception("Authentication error")
  }

  fun back() {
    navController.popBackStack()
  }
}
