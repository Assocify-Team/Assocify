package com.github.se.assocify

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.SupabaseClient
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.localsave.LoginSave
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.navigation.mainNavGraph

@Composable
fun AssocifyApp(loginSaver: LoginSave) {
  val navController = rememberNavController()
  val navActions = NavigationActions(navController, loginSaver)
  val userAPI = UserAPI(SupabaseClient.supabaseClient)
  val associationAPI = AssociationAPI(SupabaseClient.supabaseClient)
  val eventAPI = EventAPI(SupabaseClient.supabaseClient)
  val taskAPI = TaskAPI(SupabaseClient.supabaseClient)
  val budgetAPI = BudgetAPI(SupabaseClient.supabaseClient)
  val balanceAPI = BalanceAPI(SupabaseClient.supabaseClient)
  loginSaver.loadUserInfo()

  val firstDest =
      if (CurrentUser.userUid != null && CurrentUser.associationUid != null) {
        Destination.Home.route
      } else {
        Destination.Login.route
      }

  NavHost(navController = navController, startDestination = firstDest) {
    mainNavGraph(
        navActions = navActions,
        userAPI = userAPI,
        associationAPI = associationAPI,
        eventAPI = eventAPI,
        budgetAPI = budgetAPI,
        balanceAPI = balanceAPI,
        taskAPI = taskAPI)
  }
}
