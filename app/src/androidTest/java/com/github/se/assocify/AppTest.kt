package com.github.se.assocify

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.localsave.LocalSave
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.login.loginGraph
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondBadRequest
import io.mockk.every
import io.mockk.mockk
import java.io.File
import java.nio.file.Path
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Composable
fun LoginApp() {
  val navController = rememberNavController()
  val mockLoginSave: LocalSave = mockk(relaxed = true)
  val navActions = NavigationActions(navController, mockLoginSave)
  val supabaseClient: SupabaseClient =
      createSupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY) {
        install(Postgrest)
        install(Storage)
        httpEngine = MockEngine { respondBadRequest() }
      }

  val fileMock = mockk<File>()
  val cachePath = mockk<Path>()

  every { fileMock.exists() } returns false
  every { fileMock.mkdirs() } returns true
  every { fileMock.delete() } returns true
  every { fileMock.lastModified() } returns 0L
  every { fileMock.toPath() } returns cachePath
  every { fileMock.renameTo(any()) } returns true

  every { cachePath.resolve(any<String>()) } returns cachePath
  every { cachePath.resolve(any<Path>()) } returns cachePath
  every { cachePath.toFile() } returns fileMock

  val userAPI = UserAPI(supabaseClient, cachePath)
  NavHost(navController = navController, startDestination = Destination.Login.route) {
    loginGraph(navigationActions = navActions, userAPI = userAPI)
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

  @Before
  fun setup() {
    composeTestRule.setContent { MainActivity() }
  }

  @Test
  fun loginStart() {

    with(composeTestRule) {
      onRoot().assertExists()
      onNodeWithTag("LoginScreen").isDisplayed()
    }
  }

  @Test
  fun homeStart() {
    CurrentUser.userUid = "testUser"
    CurrentUser.associationUid = "testAssociation"
    with(composeTestRule) {
      onRoot().assertExists()
      onNodeWithTag("HomeScreen").isDisplayed()
    }
  }
}
