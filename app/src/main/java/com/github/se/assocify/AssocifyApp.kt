package com.github.se.assocify

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.navigation.mainNavGraph
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AssocifyApp() {
  val navController = rememberNavController()
  val navActions = NavigationActions(navController)
  val db: FirebaseFirestore = FirebaseFirestore.getInstance()
  val userAPI = UserAPI(db)
  val associationAPI = AssociationAPI(db)

  NavHost(navController = navController, startDestination = Destination.Login.route) {
    mainNavGraph(navActions = navActions, userAPI = userAPI, associationAPI = associationAPI)
  }
}
