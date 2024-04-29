package com.github.se.assocify

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.login.loginGraph
import com.google.firebase.firestore.FirebaseFirestore
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondBadRequest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Composable
fun LoginApp() {
  val navController = rememberNavController()
  val navActions = NavigationActions(navController)
  val db: FirebaseFirestore = FirebaseFirestore.getInstance()
  val supabaseClient: SupabaseClient =
      createSupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY) {
        install(Postgrest)
        httpEngine = MockEngine { respondBadRequest() }
      }
  val userAPI = UserAPI(db)
  val associationAPI = AssociationAPI(supabaseClient)
  NavHost(navController = navController, startDestination = Destination.Login.route) {
    loginGraph(navigationActions = navActions, userAPI = userAPI, associationAPI = associationAPI)
  }
}

@RunWith(AndroidJUnit4::class)
class LoginAppTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun loginStart() {
    composeTestRule.setContent { LoginApp() }
    with(composeTestRule) { onRoot().assertIsDisplayed() }
  }
}

@RunWith(AndroidJUnit4::class)
class ActivityAppTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun loginStart() {
    composeTestRule.setContent { MainActivity() }
    with(composeTestRule) { onRoot().assertExists() }
  }
}
