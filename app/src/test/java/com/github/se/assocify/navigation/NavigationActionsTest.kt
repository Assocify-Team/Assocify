package com.github.se.assocify.navigation

import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationActionsTest {
  @get:Rule val mockkRule = MockKRule(this)

  private var destination: String? = null
  @RelaxedMockK
  lateinit var mockNavController: NavHostController

  @Before
  fun setupLogin() {
    every { mockNavController.navigate(route = any(), builder = any()) } answers
        {
          destination = firstArg()
        }
  }

  @Test
  fun test() {
    val navActions = NavigationActions(mockNavController)
    navActions.navigateToMainTab(Destination.Home)
    assert(destination == Destination.Home.route)
    /*try {
      navActions.navigateTo(Destination.Login)
    } catch (e: IllegalArgumentException) {
      assert(e.message == "Destination Login is not a main tab")
    }*/
  }

  /**
   * Test the onLogin function when user exists
   */
  @Test
  fun onLoginTestToHome() {
    val navActions = NavigationActions(mockNavController)
    val userExists = true
    navActions.onLogin(userExists)
    assert(destination == Destination.Home.route)
  }

  /**
   * Test the onLogin function when user does not exist
   */
  @Test
  fun onLoginTestToSelect() {
    val navActions = NavigationActions(mockNavController)
    val userExists = false
    navActions.onLogin(userExists)
    assert(destination == Destination.SelectAsso.route){
        "Expected destination to be ${Destination.SelectAsso.route}, but was $destination"
    }
  }
}
