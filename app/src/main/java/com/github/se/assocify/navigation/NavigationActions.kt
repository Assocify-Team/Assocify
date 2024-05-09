package com.github.se.assocify.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.github.se.assocify.model.localsave.LoginSave

class NavigationActions(
    private val navController: NavHostController,
    private val loginSaver: LoginSave
) {
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
      loginSaver.saveUserInfo()
      navController.navigate(Destination.Home.route) {
        popUpTo(navController.graph.id) { inclusive = true }
      }
    } else {
      navController.navigate(Destination.SelectAsso.route) {
        popUpTo(navController.graph.id) { inclusive = true }
      }
    }
  }

  fun onLogout() {
    loginSaver.clearSavedUserInfo()
    navController.navigate(Destination.Login.route) {
      popUpTo(navController.graph.id) { inclusive = true }
    }
  }

  fun onAuthError() {
    Log.e("Authentication", "Error occurred during authentication")
  }

  fun back() {
    navController.popBackStack()
  }

  /**
   * Checks when the user is coming back from the select association screen if the previous screen
   * was the profile screen
   */
  fun backFromSelectAsso(): Boolean {
    navController.previousBackStackEntry?.destination?.route?.let {
      return (it == Destination.Profile.route)
    }
    return false
  }

  /**
   * Navigation from the create association screen
   *
   * Goes either to home if it's after login or back to profile
   */
  @SuppressLint("RestrictedApi")
  fun goFromCreateAsso() {
    val maybeProfile =
        navController.currentBackStack.value.findLast {
          it.destination.route == Destination.Profile.route
        }

    if (maybeProfile != null) {
      navController.popBackStack(maybeProfile.destination.id, false)
    } else {
      onLogin(true)
    }
  }
}
