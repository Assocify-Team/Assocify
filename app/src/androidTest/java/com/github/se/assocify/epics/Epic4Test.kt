package com.github.se.assocify.epics

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.BalanceAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.database.ReceiptAPI
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.PermissionRole
import com.github.se.assocify.model.entities.RoleType
import com.github.se.assocify.model.entities.User
import com.github.se.assocify.model.localsave.LoginSave
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
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
 * This test is an end-to-end test for the fourth epic :
 *
 * As a president of 2 associations, I want to register both in the app and chose in which one I
 * currently want to work
 */
@RunWith(AndroidJUnit4::class)
class Epic4Test : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navController: TestNavHostController
  private lateinit var navActions: NavigationActions

  private val members =
      listOf(
          User("1", "jean"),
          User("2", "roger"),
          User("3", "marie"),
      )

  private val placeholderAssociations =
      listOf(
          Association("a", "asso1", "desc1", LocalDate.EPOCH),
          Association("b", "asso2", "desc2", LocalDate.EPOCH),
      )

  private var listAsso: List<Association> = emptyList()

  private val associationAPI =
      mockk<AssociationAPI>() {
        every { getAssociations(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(List<Association>) -> Unit>()
              onSuccessCallback.invoke(listAsso)
            }
        every { addAssociation(any(), any(), any()) } answers
            {
              val asso = firstArg<Association>()

              if (asso.name == "asso1") {
                CurrentUser.associationUid = placeholderAssociations[0].uid
                listAsso = listAsso + placeholderAssociations[0]
              } else if (asso.name == "asso2") {
                CurrentUser.associationUid = placeholderAssociations[1].uid
                listAsso = listAsso + placeholderAssociations[1]
              }

              navActions.goFromCreateAsso()
            }

        every { getAssociation("a", any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(Association) -> Unit>()
              onSuccessCallback.invoke(listAsso[0])
            }

        every { getAssociation("b", any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(Association) -> Unit>()
              onSuccessCallback.invoke(listAsso[1])
            }

        every { associationNameValid(any()) } returns true
      }

  private val userAPI =
      mockk<UserAPI>() {
        every { getUser("1", any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(User) -> Unit>()
              onSuccessCallback.invoke(members[0])
            }
        every { getUser("2", any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(User) -> Unit>()
              onSuccessCallback.invoke(members[1])
            }
        every { getAllUsers(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(List<User>) -> Unit>()
              onSuccessCallback(members)
            }
        every { getCurrentUserAssociations(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(List<Association>) -> Unit>()
              onSuccessCallback.invoke(listAsso)
            }
        every { getCurrentUserRole(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(PermissionRole) -> Unit>()
              if (CurrentUser.associationUid == "a") {
                onSuccessCallback.invoke(PermissionRole("1", "a", RoleType.PRESIDENCY))
              } else {
                onSuccessCallback.invoke(PermissionRole("1", "b", RoleType.PRESIDENCY))
              }
            }

        // Never return a profile picture, permanently "fetch" the profile picture
        every { getProfilePicture(any(), any(), any()) } answers {}

        every { setProfilePicture(any(), any(), any(), any()) } answers
            {
              val onSuccessCallback = thirdArg<() -> Unit>()
              onSuccessCallback()
            }
      }

  private val eventAPI = mockk<EventAPI>() {}

  private val taskAPI = mockk<TaskAPI>(relaxUnitFun = true)

  private val budgetAPI = mockk<BudgetAPI>(relaxUnitFun = true)

  private val balanceAPI = mockk<BalanceAPI>(relaxUnitFun = true)

  private val receiptAPI = mockk<ReceiptAPI>(relaxUnitFun = true)

  private val accountingSubCategoryAPI = mockk<AccountingSubCategoryAPI>(relaxUnitFun = true)

  private val accountingCategoryAPI = mockk<AccountingCategoryAPI>(relaxUnitFun = true)

  private val loginSave = mockk<LoginSave>(relaxUnitFun = true)

  @Before
  fun testSetup() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      navActions = NavigationActions(navController, loginSave)

      TestAssocifyApp(
          navController,
          navActions,
          userAPI,
          associationAPI,
          eventAPI,
          budgetAPI,
          balanceAPI,
          taskAPI,
          receiptAPI,
          accountingCategoryAPI,
          accountingSubCategoryAPI,
          Destination.SelectAsso)
    }
  }

  @Test
  fun Epic4Test() {
    with(composeTestRule) {
      // After login as uid "1", the user is in selectAsso :
      // check it's well displayed (no back arrow, no asso yet...)
      onNodeWithTag("HelloText").assertTextContains("Hello jean !!").assertIsDisplayed()
      onNodeWithTag("GoBackButton").assertDoesNotExist()
      onNodeWithText("There are no associations to display.").assertIsDisplayed()

      // As the president, they want to create their association1
      // Goes to createAsso
      onNodeWithTag("CreateNewOrganizationButton").assertIsDisplayed().performClick()
      val toCreateAsso = navController.currentBackStackEntry?.destination?.route
      assert(toCreateAsso == Destination.CreateAsso.route)

      // Fill all the fields with the association1 data (themselves as president + 1 treasury)
      onNodeWithTag("name").performTextInput("asso1")
      onNodeWithTag("addMember").performClick()
      onNodeWithTag("memberSearchField").performClick().performTextInput("j")
      onNodeWithTag("userDropdownItem-1").performClick() // jean
      onNodeWithTag("role-PRESIDENCY").assertIsDisplayed().performClick()
      onNodeWithTag("addMemberButton").performClick()
      onNodeWithTag("addMember").performClick()
      onNodeWithTag("memberSearchField").performClick().performTextInput("mar")
      onNodeWithTag("userDropdownItem-3").performClick() // marie
      onNodeWithTag("role-TREASURY").assertIsDisplayed().performClick()
      onNodeWithTag("addMemberButton").performClick()
      onNodeWithTag("create").assertHasClickAction().assertIsEnabled()
      onNodeWithTag("create").performClick()
      verify { associationAPI.addAssociation(any(), any(), any()) }
      assert(listAsso.contains(placeholderAssociations[0]))

      // Arrives at treasury screen with association1 as the current association
      val toHome = navController.currentBackStackEntry?.destination?.route
      assert(toHome == Destination.Treasury.route)
      onNodeWithTag("treasuryScreen").assertIsDisplayed()

      // Goes to the profile screen to check that and go to add their other association
      onNodeWithTag("mainNavBarItem/profile").assertIsDisplayed().performClick()
      val toProfile = navController.currentBackStackEntry?.destination?.route
      assert(toProfile == Destination.Profile.route)

      // Click on join an other
      onNodeWithTag("associationDropdown").performClick()
      onNodeWithTag("DropdownItem-join").performClick()

      // Arrives at selectAsso, goes to createAsso
      val toSelectAssoFromProfile = navController.currentBackStackEntry?.destination?.route
      assert(toSelectAssoFromProfile == Destination.SelectAsso.route)
      onNodeWithTag("CreateNewOrganizationButton").assertIsDisplayed().performClick()
      val toCreateAssoFromProfile = navController.currentBackStackEntry?.destination?.route
      assert(toCreateAssoFromProfile == Destination.CreateAsso.route)

      // Fill all the fields with the association2 data (only themselves as president)
      onNodeWithTag("name").performTextInput("asso2")
      onNodeWithTag("addMember").performClick()
      onNodeWithTag("memberSearchField").performClick().performTextInput("j")
      onNodeWithTag("userDropdownItem-1").performClick() // jean
      onNodeWithTag("role-PRESIDENCY").assertIsDisplayed().performClick()
      onNodeWithTag("addMemberButton").performClick()
      onNodeWithTag("create").assertHasClickAction().assertIsEnabled()
      onNodeWithTag("create").performClick()
      verify { associationAPI.addAssociation(any(), any(), any()) }
      assert(listAsso.contains(placeholderAssociations[1]))

      // Arrives back at profile with association2 as the current association
      val toProfileFromCreateAsso = navController.currentBackStackEntry?.destination?.route
      assert(toProfileFromCreateAsso == Destination.Profile.route)

      // Check that association2 is the current asso and that they can switch back to asso1
      onNodeWithText("asso2").assertIsDisplayed()
      onNodeWithTag("associationDropdown").performClick()
      onNodeWithTag("DropdownItem-join").assertIsDisplayed()
      onNodeWithTag("DropdownItem-a").performClick()
      onNodeWithText("asso1").assertIsDisplayed()
    }
  }
}
