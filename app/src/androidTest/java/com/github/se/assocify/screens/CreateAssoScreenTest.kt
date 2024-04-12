package com.github.se.assocify.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.github.se.assocify.testCurrentUser
import com.github.se.assocify.ui.screens.createAsso.CreateAssoScreen
import com.github.se.assocify.ui.screens.createAsso.CreateAssoViewmodel
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CreateAssoScreenTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val userAPI = mockk<UserAPI>()
  private val assoAPI = mockk<AssociationAPI>()

  private val bigList =
      listOf(
          User("1", "jean", Role("")),
          User("2", "roger", Role("")),
          User("3", "jacques", Role("")),
          User("4", "marie", Role("")),
          User("5", "killian", Role("")),
          User("6", "paul", Role("")),
          User("7", "james", Role("")),
          User("8", "julie", Role("")),
          User("9", "bill", Role("")),
          User("10", "seb", Role("")))

  private val bigView = CreateAssoViewmodel(testCurrentUser)

  @Before
  fun setupLogin() {
    every { userAPI.getAllUsers(any(), any()) } answers
        {
          val onSuccess = firstArg<(List<User>) -> Unit>()
          onSuccess(bigList) // instantiate with a empty list: just to test login
        }
    composeTestRule.setContent { CreateAssoScreen(testCurrentUser, bigView) }
  }

  @Test
  fun display() {
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
      assert(bigView.uiState.value.editMember?.getName() == "jacques")
      assert(bigView.uiState.value.editMember!!.hasRole(""))
      onNodeWithTag("addMemberButton").assertIsDisplayed()
      onNodeWithTag("deleteMember").assertIsDisplayed()
      onNodeWithTag("addMemberButton").performClick()
      assert(bigView.uiState.value.members.size == 1)
      assert(bigView.uiState.value.members[0].getName() == "jacques")
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
      assert(bigView.uiState.value.editMember?.getName() == "jacques")
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
      onNodeWithTag("role-PRESIDENCE").performClick()
      onNodeWithTag("addMemberButton").performClick()

      onNodeWithTag("create").performClick()
      // check that the asso is created
    }
  }
}
