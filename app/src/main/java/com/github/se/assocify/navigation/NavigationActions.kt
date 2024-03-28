package com.github.se.assocify.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser

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

  fun onLogin(user: FirebaseUser?) {
    /* Handle login */
  }

  fun onAuthError() {
    /* Handle auth error */
  }

  fun onCreateReceipt() {
    /*TODO create new uid*/
    navController.navigate(Destination.Receipt("new").route)
  }
}
