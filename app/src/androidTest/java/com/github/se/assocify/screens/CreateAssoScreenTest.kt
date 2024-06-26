package com.github.se.assocify.screens

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.RoleType
import com.github.se.assocify.model.entities.User
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.createAssociation.CreateAssociationScreen
import com.github.se.assocify.ui.screens.createAssociation.CreateAssociationViewmodel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateAssoScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

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

  private val mockNavActions = mockk<NavigationActions>(relaxUnitFun = true)
  private val mockAssocAPI =
      mockk<AssociationAPI>() {
        every { addAssociation(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<() -> Unit>()
              onSuccessCallback()
            }
        every { initAssociation(any(), any(), any(), any()) } answers
            {
              val onSuccessCallback = thirdArg<() -> Unit>()
              onSuccessCallback()
            }
        every { associationNameValid(any()) } answers
            {
              val name = firstArg<String>()
              name != "nonValidName"
            }

        every { setLogo(any(), any(), any(), any()) } answers
            {
              val onSuccessCallback = thirdArg<() -> Unit>()
              onSuccessCallback()
            }
      }
  private val mockUserAPI =
      mockk<UserAPI> {
        every { getAllUsers(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(List<User>) -> Unit>()
              onSuccessCallback(bigList)
            }
        every { updateCurrentUserAssociationCache(any(), any()) } answers {}
      }

  val bigView = CreateAssociationViewmodel(mockAssocAPI, mockUserAPI, mockNavActions)

  @Before
  fun setup() {
    CurrentUser.userUid = "1"
    composeTestRule.setContent { CreateAssociationScreen(mockNavActions, bigView) }
  }

  @Test
  fun displaySmall() {
    with(composeTestRule) {
      onNodeWithTag("createAssoScreen").assertIsDisplayed()
      onNodeWithTag("TopAppBar").assertIsDisplayed()
      onNodeWithTag("logo").assertIsDisplayed()
      onNodeWithTag("name").assertIsDisplayed()
      onNodeWithTag("addMember").assertIsDisplayed()
      onNodeWithTag("create").assertIsDisplayed()
    }
  }

  @Test
  fun testAddMember() {
    with(composeTestRule) {
      onNodeWithTag("addMember").performClick()
      onNodeWithTag("memberSearchField").assertIsDisplayed()
      onNodeWithTag("memberSearchField").performClick().performTextInput("j")
      assert(bigView.uiState.value.searchMember == "j")
      assert(bigView.uiState.value.searchMemberList.size == 4)
      onNodeWithTag("userDropdownItem-1").assertIsDisplayed()
      onNodeWithTag("userDropdownItem-3").assertIsDisplayed()
      onNodeWithTag("userDropdownItem-7").assertIsDisplayed()
      onNodeWithTag("userDropdownItem-8").assertIsDisplayed()
      onNodeWithTag("userDropdownItem-3").performClick() // jacques
      assert(bigView.uiState.value.editMember?.user?.name == "jacques")
      onNodeWithTag("addMemberButton").assertIsDisplayed()
      onNodeWithTag("deleteMember").assertIsDisplayed()
      onNodeWithTag("addMemberButton").performClick()
      assert(bigView.uiState.value.members.size == 1)
      assert(bigView.uiState.value.members[0].user.name == "jacques")
      onNodeWithTag("MemberListItem-jacques").assertIsDisplayed()
    }
  }

  @Test
  fun testDeleteMember() {
    with(composeTestRule) {
      onNodeWithTag("addMember").performClick()
      onNodeWithTag("memberSearchField").performClick().performTextInput("j")
      onNodeWithTag("userDropdownItem-3").performClick() // jacques
      onNodeWithTag("addMemberButton").performClick()
      onNodeWithTag("MemberListItem-jacques").assertIsDisplayed()
      onNodeWithTag("editMember-jacques").performClick()
      assert(bigView.uiState.value.editMember?.user?.name == "jacques")
      onNodeWithTag("deleteMember").assertIsDisplayed()
      onNodeWithTag("deleteMember").performClick()
      assert(bigView.uiState.value.members.isEmpty())
      onNodeWithTag("MemberListItem-jacques").assertDoesNotExist()
    }
  }

  @Test
  fun testCreateAsso() {
    with(composeTestRule) {
      onNodeWithTag("name").performTextInput("assoName")
      onNodeWithTag("addMember").performClick()
      onNodeWithTag("memberSearchField").performClick().performTextInput("j")
      onNodeWithTag("userDropdownItem-1").performClick() // jean
      onNodeWithTag("role-PRESIDENCY").assertIsDisplayed()
      onNodeWithTag("role-PRESIDENCY").performClick()
      assert(bigView.uiState.value.editMember!!.role.type == RoleType.PRESIDENCY)
      onNodeWithTag("addMemberButton").performClick()
      onNodeWithTag("create").assertHasClickAction()
      onNodeWithTag("create").assertIsEnabled()
      onNodeWithTag("create").performClick()
      verify { mockAssocAPI.addAssociation(any(), any(), any()) }
    }
  }

  @Test
  fun testCantCreateAsso() {
    bigView.setLogo(mockk())
    with(composeTestRule) {
      onNodeWithTag("create").assertIsNotEnabled()
      onNodeWithTag("name").performTextInput("nonValidName")
      onNodeWithText("Name is invalid or already used").assertIsDisplayed()
      onNodeWithTag("create").assertIsNotEnabled()
      onNodeWithTag("name").performTextClearance()
      onNodeWithTag("name").performTextInput("assoName")
      assert(bigView.uiState.value.nameError == null)
      onNodeWithTag("addMember").performClick()
      onNodeWithTag("memberSearchField").performClick().performTextInput("j")
      onNodeWithTag("userDropdownItem-1").performClick() // jean
      onNodeWithTag("addMemberButton").performClick()
      onNodeWithTag("editMember-jean").performClick()
      onNodeWithTag("role-PRESIDENCY").performClick()
      onNodeWithTag("addMemberButton").performClick()
      onNodeWithTag("create").performClick()
      verify { mockAssocAPI.addAssociation(any(), any(), any()) }
    }
  }

  fun testCreateButton() {
    with(composeTestRule) {
      onNodeWithTag("create").performClick()
      verify { bigView.saveAsso() }
      verify { mockNavActions.navigateToMainTab(Destination.Treasury) }
    }
  }

  @Test
  fun testGoBackButton() {
    with(composeTestRule) {
      onNodeWithTag("Back").performClick()
      verify { mockNavActions.back() }
    }
  }

  @Test
  fun openProfileSheet() {
    with(composeTestRule) {
      onNodeWithTag("logo").performClick()
      onNodeWithTag("photoSelectionSheet").assertIsDisplayed()
      bigView.signalCameraPermissionDenied()
      onNodeWithTag("snackbar").assertIsDisplayed()
    }
  }

  @Test
  fun setUri() {
    bigView.setLogo(mockk())
    with(composeTestRule) { onNodeWithTag("logo").assertIsDisplayed() }
  }
}
