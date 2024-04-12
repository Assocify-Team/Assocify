package com.github.se.assocify

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.navigation.mainNavGraph
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun AssocifyApp() {
  val navController = rememberNavController()
  val navActions = NavigationActions(navController)
  val db = Firebase.firestore

  NavHost(navController = navController, startDestination = Destination.Home.route) {
    mainNavGraph(navActions = navActions)
  }
}
