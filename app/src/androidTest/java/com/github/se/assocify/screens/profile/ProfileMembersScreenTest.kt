package com.github.se.assocify.screens.profile

import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
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

  private var userList =
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

  private var userRole: MutableMap<String, RoleType> =
      mutableMapOf(
          "1" to RoleType.PRESIDENCY,
          "2" to RoleType.TREASURY,
          "3" to RoleType.MEMBER,
          "4" to RoleType.MEMBER,
          "5" to RoleType.MEMBER,
          "6" to RoleType.MEMBER,
          "7" to RoleType.MEMBER,
          "8" to RoleType.MEMBER,
          "9" to RoleType.MEMBER,
          "10" to RoleType.MEMBER,
          "11" to RoleType.MEMBER,
      )

  private val applicantList = userList.take(2)

  private var assoMembers: List<AssociationMember> =
      userList.map {
        AssociationMember(
            it,
            Association("a", "assoName", "", LocalDate.EPOCH),
            PermissionRole("r", "a", userRole[it.uid]!!))
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
        every { updateCache(any(), any()) } answers
            {
              firstArg<(Map<String, Association>) -> Unit>()
                  .invoke(mapOf("a" to Association("a", "assoName", "", LocalDate.EPOCH)))
            }
      }

  private val userAPI =
      mockk<UserAPI> {
        every { removeUserFromAssociation(any(), any(), any(), any()) } answers
            {
              assoMembers = assoMembers.filter { it.user.uid != firstArg<String>() }
              val onSuccess = thirdArg<() -> Unit>()
              onSuccess()
            }
        every { changeRoleOfUser(any(), any(), any(), any(), any()) } answers
            {
              userRole[firstArg<String>()] = thirdArg<RoleType>()
              val onSuccess = arg<() -> Unit>(3)
              onSuccess()
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

      onNodeWithText("Current members").performScrollTo().assertIsDisplayed()
      assoMembers.forEach {
        Log.e("assoMembers", it.user.uid)
        onNodeWithTag("membersScreen").performScrollToNode(hasTestTag("memberItem-${it.user.uid}"))
        onNodeWithTag("memberItem-${it.user.uid}").assertIsDisplayed()
      }
    }
  }

  @Test
  fun editMember() {
    with(composeTestRule) {
      val member = assoMembers[0]
      val originalRole = member.role.type.name
      onNodeWithTag("memberItem-${member.user.uid}").assertTextContains(originalRole)
      onNodeWithTag("editButton-0").performClick()
      onNodeWithText("Change ${member.user.name}'s role ?").assertIsDisplayed()
      onNodeWithTag("role-${originalRole}").assertIsSelected()
      onNodeWithTag("role-${RoleType.PRESIDENCY.name}").performClick()
      onNodeWithTag("confirmButton").performClick()
      onNodeWithTag("memberItem-${member.user.uid}").assertTextContains(RoleType.PRESIDENCY.name)
      onNodeWithTag("editButton-0").performClick()
      onNodeWithTag("role-${RoleType.PRESIDENCY.name}").assertIsSelected()
      onNodeWithTag("role-${originalRole}").performClick()
      onNodeWithTag("cancelButton").performClick()
      onNodeWithTag("memberItem-${member.user.uid}").assertTextContains(RoleType.PRESIDENCY.name)
    }
  }

  @Test
  fun deleteMember() {
    with(composeTestRule) {
      val member = assoMembers[1]
      onNodeWithTag("memberItem-${assoMembers[0].user.uid}").assertIsDisplayed()
      onNodeWithTag("deleteMemberButton-0").performClick()
      onNodeWithText("You cannot remove yourself").assertIsDisplayed()
      onNodeWithTag("deleteMemberButton-1").performClick()
      onNodeWithText("Are you sure you want to remove ${member.user.name} from the association?")
          .assertIsDisplayed()
      onNodeWithTag("cancelButton").performClick()
      onNodeWithTag("deleteMemberButton-1").performClick()
      onNodeWithTag("confirmButton").performClick()
      onNodeWithTag("memberItem-${member.user.uid}").assertDoesNotExist()
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
