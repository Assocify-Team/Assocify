package com.github.se.assocify

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.login.loginGraph
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Composable
fun LoginApp() {
  val navController = rememberNavController()
  val navActions = NavigationActions(navController)
  NavHost(navController = navController, startDestination = Destination.Login.route) {
    loginGraph(navigationActions = navActions)
  }
}

@RunWith(AndroidJUnit4::class)
class AppTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun display() {
    composeTestRule.setContent { AssocifyApp() }
    with(composeTestRule) {
      onRoot().assertIsDisplayed()
      onNodeWithTag("mainNavBarItem/treasury").assertIsDisplayed().performClick()
      onRoot().assertIsDisplayed()
      onNodeWithTag("mainNavBarItem/profile").assertIsDisplayed().performClick()
      onRoot().assertIsDisplayed()
      onNodeWithTag("mainNavBarItem/chat").assertIsDisplayed().performClick()
      onRoot().assertIsDisplayed()
      onNodeWithTag("mainNavBarItem/event").assertIsDisplayed().performClick()
      onRoot().assertIsDisplayed()
    }
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
