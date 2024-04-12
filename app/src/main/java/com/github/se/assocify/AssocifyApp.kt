package com.github.se.assocify

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.navigation.mainNavGraph

@Composable
fun AssocifyApp() {
  val navController = rememberNavController()
  val navActions = NavigationActions(navController)
  NavHost(navController = navController, startDestination = Destination.Home.route) {
    // Need to have a currentUser, however we can't still tie it to the real auth system so this is
    // left as such
    mainNavGraph(navActions = navActions, currentUser = CurrentUser("testUser", "testAssociation"))
  }
}

g
