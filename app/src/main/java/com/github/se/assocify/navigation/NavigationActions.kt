package com.github.se.assocify.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.github.se.assocify.R

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
}



