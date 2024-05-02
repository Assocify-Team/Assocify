package com.github.se.assocify

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.github.se.assocify.model.SupabaseClient
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.navigation.mainNavGraph

@Composable
fun AssocifyApp() {
  val navController = rememberNavController()
  val navActions = NavigationActions(navController)
  val userAPI = UserAPI(SupabaseClient.supabaseClient)
  val associationAPI = AssociationAPI(SupabaseClient.supabaseClient)
  val eventAPI = EventAPI(SupabaseClient.supabaseClient)
  NavHost(navController = navController, startDestination = Destination.Login.route) {
    mainNavGraph(
        navActions = navActions,
        userAPI = userAPI,
        associationAPI = associationAPI,
        eventAPI = eventAPI)
  }
}
