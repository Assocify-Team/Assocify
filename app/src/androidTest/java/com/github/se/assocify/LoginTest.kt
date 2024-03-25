package com.github.se.assocify

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
import com.github.se.assocify.ui.LoginPage
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode
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

  class LoginScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
      ComposeScreen<LoginScreen>(
          semanticsProvider = semanticsProvider,
          viewBuilderAction = { hasTestTag("LoginScreen") }) {

    // Structural elements of the UI
    val loginTitle: KNode = child { hasTestTag("LoginTitle") }
    val loginButton: KNode = child { hasTestTag("LoginButton") }
  }

  private var authError = false
  private var authSuccess = false

  @Before
  fun setupLogin() {

    composeTestRule.setContent { LoginPage({ authSuccess = true }, { authError = true }) }
  }

  @Test
  fun titleAndButtonAreCorrectlyDisplayed() {
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
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
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      val intent = Intent()
      val activity = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)

      intending(toPackage("com.google.android.gms")).respondWith(activity)

      loginButton {
        assertIsDisplayed()
        performClick()
      }

      // assert that an Intent resolving to Google Mobile Services has been sent (for sign-in)
      intended(toPackage("com.google.android.gms"))

      assert(authError)
      authError = false

      assert(!authSuccess)
    }
  }
}
