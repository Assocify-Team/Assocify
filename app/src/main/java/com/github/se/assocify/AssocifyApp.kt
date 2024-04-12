package com.github.se.assocify

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.selectAssoc.SelectAssociation
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun AssocifyApp() {
  val navController = rememberNavController()
  val navActions = NavigationActions(navController)
  val db = Firebase.firestore
  SelectAssociation(
      navActions = navActions, associationAPI = AssociationAPI(db), userAPI = UserAPI(db))
  /*
  NavHost(navController = navController, startDestination = Destination.Home.route) {
    mainNavGraph(navActions = navActions)
  }*/
}
