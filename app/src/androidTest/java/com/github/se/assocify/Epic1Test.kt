package com.github.se.assocify

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.BudgetAPI
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

/**
 * This test is an end-to-end test for the first epic :
 *
 * As the president of my association, I want to register myself, the association and its members in
 * the application, and then go to my profile to modify my information
 */
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
          uid = "aaa",
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
        every { addAssociation(any(), any(), any()) } answers
            {
              CurrentUser.associationUid = asso.uid
              navActions.onLogin(true)
            }
      }

  private val userAPI =
      mockk<UserAPI>() {
        every { getAllUsers(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(List<User>) -> Unit>()
              onSuccessCallback(bigList)
            }
        every { getUser(any() /*"1"?*/, any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(User) -> Unit>()
              onSuccessCallback.invoke(bigList[0])
            }

        every { setDisplayName("1", "antoine", any(), any()) } answers {}

        every { getCurrentUserAssociations(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(List<Association>) -> Unit>()
              onSuccessCallback.invoke(listOf(asso))
            }
      }

  private val eventAPI = mockk<EventAPI>() { every { getEvents(any(), any()) } answers {} }

  private val taskAPI = mockk<TaskAPI>(relaxUnitFun = true)

  private val budgetAPI = mockk<BudgetAPI>(relaxUnitFun = true)

  private val loginSave = mockk<LoginSave>(relaxUnitFun = true)

  @Before
  fun testSetup() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      navActions = NavigationActions(navController, loginSave)

      TestAssocifyApp(
          navController, navActions, userAPI, associationAPI, eventAPI, budgetAPI, taskAPI)
    }
  }

  @Test
  fun epic1Test() {
    with(composeTestRule) {
      // register my association
      onNodeWithTag("DisplayOrganizationScreen-aaa").assertIsDisplayed()
      onNodeWithTag("CreateNewOrganizationButton").assertIsDisplayed().performClick()
      val toCreateAsso = navController.currentBackStackEntry?.destination?.route
      assert(toCreateAsso == Destination.CreateAsso.route)

      // create my association with its members
      onNodeWithTag("name").performTextInput("assoName")
      onNodeWithTag("addMember").performClick()
      onNodeWithTag("memberSearchField").performClick().performTextInput("j")
      onNodeWithTag("userDropdownItem-1").performClick() // jean
      onNodeWithTag("role-PRESIDENCY").assertIsDisplayed()
      onNodeWithTag("role-PRESIDENCY").performClick()
      onNodeWithTag("addMemberButton").performClick()
      onNodeWithTag("addMember").performClick()
      onNodeWithTag("memberSearchField").performClick().performTextInput("seb")
      onNodeWithTag("userDropdownItem-10").performClick() // jean
      onNodeWithTag("role-TREASURY").assertIsDisplayed().performClick()
      onNodeWithTag("addMemberButton").performClick()
      onNodeWithTag("create").assertHasClickAction()
      onNodeWithTag("create").assertIsEnabled()
      onNodeWithTag("create").performClick()
      verify { associationAPI.addAssociation(any(), any(), any()) }

      val toHome = navController.currentBackStackEntry?.destination?.route
      assert(toHome == Destination.Home.route)

      onNodeWithTag("homeScreen").assertIsDisplayed()
      onNodeWithTag("mainNavBarItem/profile").assertIsDisplayed().performClick()

      // go to my profile
      val toProfile = navController.currentBackStackEntry?.destination?.route
      assert(toProfile == Destination.Profile.route)

      onNodeWithTag("profileScreen").assertIsDisplayed()
      onNodeWithTag("profileName").assertIsDisplayed()
      onNodeWithText("jean").assertIsDisplayed()

      // edit my name
      onNodeWithTag("editProfile").assertIsDisplayed().performClick()
      onNodeWithTag("editName").performTextClearance()
      onNodeWithTag("editName").performTextInput("antoine")
      onNodeWithTag("confirmModifyButton").assertIsDisplayed().performClick()

      // verify that the name has been modified
      verify { userAPI.setDisplayName("1", "antoine", any(), any()) }
      onNodeWithText("antoine").assertIsDisplayed()
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
    budgetAPI: BudgetAPI,
    taskAPI: TaskAPI
) {
  CurrentUser.userUid = "1"

  NavHost(navController = navController, startDestination = Destination.SelectAsso.route) {
    mainNavGraph(
        navActions = navActions,
        userAPI = userAPI,
        associationAPI = associationAPI,
        eventAPI = eventAPI,
        budgetAPI = budgetAPI,
        taskAPI)
  }
}
