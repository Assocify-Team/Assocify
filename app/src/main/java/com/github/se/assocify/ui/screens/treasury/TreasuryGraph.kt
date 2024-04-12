package com.github.se.assocify.ui.screens.treasury

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

fun NavGraphBuilder.treasuryGraph(navigationActions: NavigationActions) {
  composable(
      route = Destination.Treasury.route,
  ) {
    UserAPI(Firebase.firestore).addUser(User("testUser", "Rayan", Role("Admin")), {}, {})
    TreasuryScreen(navigationActions, CurrentUser("testUser", "testAssociation"))
  }
}
