package com.github.se.assocify

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.localsave.LoginSave
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.navigation.mainNavGraph
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Epic1Test : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navController: TestNavHostController
  private lateinit var navActions: NavigationActions

  private val associationAPI =
      mockk<AssociationAPI>() {
        every { getAssociations(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(List<Association>) -> Unit>()
              onSuccessCallback.invoke(
                  listOf(
                      Association(
                          uid = "1",
                          name = "Test",
                          description = "Test description",
                          creationDate = LocalDate.EPOCH)))
            }
      }

  private val userAPI = mockk<UserAPI>() { every { getUser(any(), any(), any()) } answers {} }

  private val eventAPI = mockk<EventAPI>() { every { getEvents(any(), any()) } answers {} }

  private val taskAPI = mockk<TaskAPI>(relaxUnitFun = true)

  private val loginSave = mockk<LoginSave>(relaxUnitFun = true)

  @Before
  fun testSetup() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      navActions = NavigationActions(navController, loginSave)

      TestAssocifyApp(navController, navActions, userAPI, associationAPI, eventAPI, taskAPI)
    }
  }

  @Test
  fun epic1Test() {
    with(composeTestRule) {
      onNodeWithTag("DisplayOrganizationScreen-Test").assertIsDisplayed()
      onNodeWithTag("CreateNewOrganizationButton").assertIsDisplayed().performClick()
      val toCreateAsso = navController.currentBackStackEntry?.destination?.route
      assert(toCreateAsso == Destination.CreateAsso.route)

      onNodeWithTag("createAssoScreen").assertIsDisplayed()
    }
  }
}

@Composable
fun TestAssocifyApp(
    navController: TestNavHostController,
    navActions: NavigationActions,
    userAPI: UserAPI,
    associationAPI: AssociationAPI,
    eventAPI: EventAPI,
    taskAPI: TaskAPI
) {
  CurrentUser.userUid = "1"

  NavHost(navController = navController, startDestination = Destination.SelectAsso.route) {
    mainNavGraph(
        navActions = navActions,
        userAPI = userAPI,
        associationAPI = associationAPI,
        eventAPI = eventAPI,
        taskAPI)
  }
}
