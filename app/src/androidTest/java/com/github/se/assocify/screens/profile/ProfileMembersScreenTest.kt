package com.github.se.assocify.screens.profile

import android.util.Log
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.AssociationMember
import com.github.se.assocify.model.entities.PermissionRole
import com.github.se.assocify.model.entities.RoleType
import com.github.se.assocify.model.entities.User
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.profile.members.ProfileMembersScreen
import com.github.se.assocify.ui.screens.profile.members.ProfileMembersViewModel
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
class ProfileMembersScreenTest :
    TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private val navActions = mockk<NavigationActions>()
  private var goBack = false

  private val userList =
      listOf(
          User("1", "Sarah"),
          User("2", "veryveryveryverylooooooooooooooooooooongnameeeeeeeeee"),
          User("3", "Alice"),
          User("4", "Bob"),
          User("5", "Charlie"),
          User("6", "David"),
          User("7", "Eve"),
          User("8", "Frank"),
          User("9", "Grace"),
          User("10", "Hank"),
          User("11", "Ivy"),
      )

  private val applicantList = userList.take(2)

  private val assoMembers: List<AssociationMember> =
      userList.map {
        AssociationMember(
            it,
            Association("a", "assoName", "", LocalDate.EPOCH),
            PermissionRole("r", "a", RoleType.MEMBER))
      }

  private val associationAPI =
      mockk<AssociationAPI> {
        every { getApplicants(any(), any(), any()) } answers
            {
              secondArg<(List<User>) -> Unit>().invoke(applicantList)
            }
        every { getMembers(any(), any(), any()) } answers
            {
              secondArg<(List<AssociationMember>) -> Unit>().invoke(assoMembers)
            }
      }

  private val userAPI =
      mockk<UserAPI> {
        every { removeUserFromAssociation(any(), any(), any(), any()) } answers
            {
              val onSuccess = thirdArg<() -> Unit>()

            }
      }

  @Before
  fun testSetup() {
    CurrentUser.userUid = "1"
    CurrentUser.associationUid = "asso"

    every { navActions.back() } answers { goBack = true }

    composeTestRule.setContent {
      ProfileMembersScreen(
          navActions = navActions, ProfileMembersViewModel(associationAPI, userAPI))
    }
  }

  @Test
  fun display() {
    with(composeTestRule) {
      onNodeWithTag("Members Screen").assertIsDisplayed()

      onNodeWithText("New requests").assertIsDisplayed()
      applicantList.forEach { onNodeWithTag("applicantCard-${it.uid}").assertIsDisplayed() }
      onAllNodesWithTag("rejectButton").assertCountEquals(applicantList.size)
      onAllNodesWithTag("acceptButton").assertCountEquals(applicantList.size)

      onNodeWithText("Current members").performScrollTo().assertIsDisplayed()
      assoMembers.forEach {
        Log.e("assoMembers", it.user.uid)
        onNodeWithTag("membersScreen").performScrollToNode(hasTestTag("memberItem-${it.user.uid}"))
        onNodeWithTag("memberItem-${it.user.uid}").assertIsDisplayed()
      }
    }
  }

  @Test
  fun goBack() {
    with(composeTestRule) {
      onNodeWithTag("backButton").performClick()
      assert(goBack)
    }
  }
}
