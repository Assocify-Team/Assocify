package com.github.se.assocify

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
import com.github.se.assocify.model.entities.User
import com.github.se.assocify.model.localsave.LoginSave
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.navigation.mainNavGraph
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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

  private val bigList =
      listOf(
          User("1", "jean"),
          User("2", "roger"),
          User("3", "jacques"),
          User("4", "marie"),
          User("5", "killian"),
          User("6", "paul"),
          User("7", "james"),
          User("8", "julie"),
          User("9", "bill"),
          User("10", "seb"))

  private val asso =
      Association(
          uid = "1",
          name = "Test",
          description = "Test description",
          creationDate = LocalDate.EPOCH)

  private val associationAPI =
      mockk<AssociationAPI>() {
        every { getAssociations(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(List<Association>) -> Unit>()
              onSuccessCallback.invoke(listOf(asso))
            }
      }

  private val userAPI =
      mockk<UserAPI>() {
        every { getAllUsers(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(List<User>) -> Unit>()
              onSuccessCallback(bigList)
            }
      }

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
      onNodeWithTag("name").performTextInput("assoName")
      onNodeWithTag("addMember").performClick()
      onNodeWithTag("memberSearchField").performClick().performTextInput("j")
      onNodeWithTag("userDropdownItem-1").performClick() // jean
      onNodeWithTag("role-PRESIDENCY").assertIsDisplayed().performClick()
      onNodeWithTag("addMemberButton").performClick()
      onNodeWithTag("create").assertHasClickAction().assertIsEnabled()
      onNodeWithTag("create").performClick()
      verify { associationAPI.addAssociation(any(), any(), any()) }

      val toHome = navController.currentBackStackEntry?.destination?.route
      assert(toHome == Destination.Home.route)
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
