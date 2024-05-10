package com.github.se.assocify.screens

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.intent.rule.IntentsRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.User
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.login.LoginScreen
import com.github.se.assocify.ui.screens.login.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  // The IntentsTestRule simply calls Intents.init() before the @Test block
  // and Intents.release() after the @Test block is completed. IntentsTestRule
  // is deprecated, but it was MUCH faster than using IntentsRule in our tests
  @get:Rule val intentsTestRule = IntentsRule()

  class LoginTestScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
      ComposeScreen<LoginTestScreen>(
          semanticsProvider = semanticsProvider,
          viewBuilderAction = { hasTestTag("LoginScreen") }) {

    // Structural elements of the UI
    val loginTitle: KNode = child { hasTestTag("LoginTitle") }
    val loginButton: KNode = child { hasTestTag("LoginButton") }
  }

  private val navActions = mockk<NavigationActions>()
  private val userAPI = mockk<UserAPI>() { every { addUser(any(), any(), any()) } answers {} }
  private val viewmodel = LoginViewModel(navActions, userAPI)

  private var authSuccess = false
  private var authError = false

  @Before
  fun setupLogin() {
    every { navActions.onLogin(any()) } answers { authSuccess = true }
    every { navActions.onAuthError() } answers { authError = true }

    every { userAPI.getAllUsers(any(), any()) } answers
        {
          val onSuccess = firstArg<(List<User>) -> Unit>()
          onSuccess(listOf()) // instantiate with a empty list: just to test login
        }
    composeTestRule.setContent { LoginScreen(navActions, viewmodel) }
  }

  @Test
  fun titleAndButtonAreCorrectlyDisplayed() {
    ComposeScreen.onComposeScreen<LoginTestScreen>(composeTestRule) {
      // Test the UI elements
      loginTitle {
        assertIsDisplayed()
        assertTextEquals("Welcome")
      }
      loginButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  @Test
  fun googleSignInReturnsValidActivityResult() {
    ComposeScreen.onComposeScreen<LoginTestScreen>(composeTestRule) {
      val intent = Intent()
      val activity = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)

      intending(toPackage("com.google.android.gms")).respondWith(activity)

      loginButton {
        assertIsDisplayed()
        performClick()
      }

      // assert that an Intent resolving to Google Mobile Services has been sent (for sign-in)
      intended(toPackage("com.google.android.gms"))

      verify(timeout = 1000) { navActions.onAuthError() }
      confirmVerified(navActions)
      assert(authError)
    }
  }

  @Test
  fun testUpdateUser1() {
    val userInfo = mockk<UserInfo>()
    val googleUser = mockk<GoogleSignInAccount>()
    every { userInfo.id } returns "testId"
    every { googleUser.displayName } returns "testName"
    every { googleUser.email } returns "testEmail"
    val userList = listOf<User>()
    every { userAPI.getAllUsers(any(), any()) } answers
        {
          firstArg<(List<User>) -> Unit>().invoke(userList)
        }
    viewmodel.updateUser(userInfo, googleUser)
    verify { navActions.onLogin(false) }
  }

  @Test
  fun testUpdateUser2() {
    val userInfo = mockk<UserInfo>()
    val googleUser = mockk<GoogleSignInAccount>()
    every { userInfo.id } returns "testId"
    every { googleUser.displayName } returns "testName"
    every { googleUser.email } returns "testEmail"
    val userList = listOf(User("testId", "testName", "testEmail"))
    every { userAPI.getAllUsers(any(), any()) } answers
        {
          firstArg<(List<User>) -> Unit>().invoke(userList)
        }
    every { userAPI.getCurrentUserAssociations(any(), any()) } answers
        {
          firstArg<(List<Association>) -> Unit>().invoke(listOf())
        }
    viewmodel.updateUser(userInfo, googleUser)
    verify { navActions.onLogin(false) }
  }

  @Test
  fun testUpdateUser3() {
    val userInfo = mockk<UserInfo>()
    val googleUser = mockk<GoogleSignInAccount>()
    every { userInfo.id } returns "testId"
    every { googleUser.displayName } returns "testName"
    every { googleUser.email } returns "testEmail"
    val userList = listOf(User("testId", "testName", "testEmail"))
    every { userAPI.getAllUsers(any(), any()) } answers
        {
          firstArg<(List<User>) -> Unit>().invoke(userList)
        }
    val assocList = listOf(Association("testId", "testName", "testDescription", LocalDate.now()))
    every { userAPI.getCurrentUserAssociations(any(), any()) } answers
        {
          firstArg<(List<Association>) -> Unit>().invoke(assocList)
        }
    viewmodel.updateUser(userInfo, googleUser)
    verify { navActions.onLogin(true) }
  }
}
