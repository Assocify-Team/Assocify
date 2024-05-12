package com.github.se.assocify.screens.profile

import android.net.Uri
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.PermissionRole
import com.github.se.assocify.model.entities.RoleType
import com.github.se.assocify.model.entities.User
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.profile.ProfileScreen
import com.github.se.assocify.ui.screens.profile.ProfileViewModel
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
class ProfileScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private val navActions = mockk<NavigationActions>()
  private var tabSelected = false
  private var goNotif = false
  private var goSecu = false
  private var goPref = false
  private var goMembers = false
  private var goRoles = false

  private val uid = "1"
  private val asso1 = Association("asso", "test", "test", LocalDate.EPOCH)
  private val asso2 = Association("asso2", "test2", "test2", LocalDate.EPOCH)
  private val mockAssocAPI =
      mockk<AssociationAPI>() {
        every { getAssociation("asso", any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(Association) -> Unit>()
              onSuccessCallback(asso1)
            }
        every { getAssociation("asso2", any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(Association) -> Unit>()
              onSuccessCallback(asso2)
            }
      }
  private val mockUserAPI =
      mockk<UserAPI>() {
        every { getUser(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(User) -> Unit>()
              onSuccessCallback(User("1", "jean"))
            }
        every { getCurrentUserAssociations(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(List<Association>) -> Unit>()
              onSuccessCallback(listOf(asso1, asso2))
            }
        every { setDisplayName(any(), "newName", any(), any()) } answers
            {
              val onSuccessCallback = thirdArg<() -> Unit>()
              onSuccessCallback()
            }

        every { getCurrentUserRole(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(PermissionRole) -> Unit>()
              onSuccessCallback(PermissionRole("1", "asso", RoleType.PRESIDENCY))
            }
      }

  private lateinit var mViewmodel: ProfileViewModel
  private val uri: Uri = Uri.parse("content://test")

  @Before
  fun testSetup() {
    CurrentUser.userUid = uid
    CurrentUser.associationUid = "asso"

    mViewmodel = ProfileViewModel(mockAssocAPI, mockUserAPI, navActions)

    every { navActions.navigateToMainTab(any()) } answers { tabSelected = true }
    every { navActions.navigateTo(Destination.ProfileNotifications) } answers { goNotif = true }
    every { navActions.navigateTo(Destination.ProfileSecurityPrivacy) } answers { goSecu = true }
    every { navActions.navigateTo(Destination.ProfilePreferences) } answers { goPref = true }
    every { navActions.navigateTo(Destination.ProfileMembers) } answers { goMembers = true }
    every { navActions.navigateTo(Destination.ProfileRoles) } answers { goRoles = true }

    composeTestRule.setContent { ProfileScreen(navActions = navActions, mViewmodel) }
  }

  @Test
  fun display() {
    with(composeTestRule) {
      onNodeWithTag("profileScreen").assertIsDisplayed()
      onNodeWithTag("default profile icon").assertIsDisplayed()
      onNodeWithTag("profileName").assertIsDisplayed()
      onNodeWithTag("profileRole").assertIsDisplayed()
      onNodeWithTag("editProfile").assertIsDisplayed()
      onNodeWithTag("associationDropdown").assertIsDisplayed()
      onNodeWithTag("Preferences").performScrollTo().assertIsDisplayed()
      onNodeWithTag("Privacy").performScrollTo().assertIsDisplayed()
      onNodeWithTag("Notifications").performScrollTo().assertIsDisplayed()
      onNodeWithTag("Members").performScrollTo().assertIsDisplayed()
      onNodeWithTag("Roles").performScrollTo().assertIsDisplayed()
      onNodeWithTag("logoutButton").performScrollTo().assertIsDisplayed()
      onNodeWithTag("associationDropdown").performScrollTo().performClick()
      onNodeWithTag("DropdownItem-${asso1.uid}").assertIsDisplayed()
      onNodeWithTag("DropdownItem-${asso2.uid}").assertIsDisplayed()
    }
  }

  // test if profile picture well displayed
  @Test
  fun displayProfilePicture() {
    with(composeTestRule) {
      onNodeWithTag("default profile icon").assertIsDisplayed()
      onNodeWithTag("default profile icon").assertHasClickAction()
      mViewmodel.setImage(uri)
      assert(mViewmodel.uiState.value.profileImageURI != null)
      onNodeWithTag("profilePicture").assertIsDisplayed()
    }
  }

  // test if you can change name
  @Test
  fun changeName() {
    with(composeTestRule) {
      onNodeWithTag("editProfile").performClick()
      onNodeWithTag("editName").assertIsDisplayed()
      onNodeWithTag("confirmModifyButton").assertIsDisplayed()
      onNodeWithTag("editName").performClick()
      onNodeWithTag("editName").performTextClearance()
      onNodeWithTag("editName").performTextInput("newName")
      onNodeWithTag("confirmModifyButton").performClick() // need to set action in test
      verify { mockUserAPI.setDisplayName(any(), "newName", any(), any()) }
      assert(mViewmodel.uiState.value.myName == "newName")
    }
  }

  // test if you can change association
  @Test
  fun changeAssociation() {
    with(composeTestRule) {
      onNodeWithTag("associationDropdown").assertIsDisplayed().performClick()
      onNodeWithTag("DropdownItem-${asso2.uid}").performClick()
      assert(mViewmodel.uiState.value.selectedAssociation.name == asso2.name)
      assert(CurrentUser.associationUid == asso2.uid)
    }
  }

  // test if correct role is displayed
  @Test
  fun correctRole() {
    with(composeTestRule) {
      onNodeWithTag("profileRole").assertIsDisplayed()
      onNodeWithText("PRESIDENCY").assertIsDisplayed()
    }
  }

  // test if you can navigate to sub screens (settings)
  @Test
  fun navigateToSubScreen() {
    with(composeTestRule) {
      onNodeWithTag("Preferences").performClick()
      assert(goPref)

      onNodeWithTag("Privacy").performClick()
      assert(goSecu)

      onNodeWithTag("Notifications").performClick()
      assert(goNotif)

      onNodeWithTag("Members").performScrollTo().performClick()
      assert(goMembers)

      onNodeWithTag("Roles").performScrollTo().performClick()
      assert(goRoles)
    }
  }

  // test if you can navigate in the main tabs
  @Test
  fun navigate() {
    with(composeTestRule) {
      onNodeWithTag("mainNavBarItem/treasury").performClick()
      assert(tabSelected)
    }
  }
}
