package com.github.se.assocify.screens.profile

import android.net.Uri
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Role
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
  private var goTheme = false

  private val uid = "1"
  private val mockAssocAPI = mockk<AssociationAPI>(relaxUnitFun = true)
  private val mockUserAPI =
      mockk<UserAPI> {
        every { getUser(any(), any(), any()) } answers { User("1", "jean", Role("role")) }
      }

  private lateinit var mViewmodel: ProfileViewModel
  private val uri: Uri = Uri.parse("content://test")

  @Before
  fun testSetup() {
    CurrentUser.userUid = uid
    CurrentUser.associationUid = "asso"

    mViewmodel = ProfileViewModel(mockAssocAPI, mockUserAPI)

    every { navActions.navigateToMainTab(any()) } answers { tabSelected = true }
    every { navActions.navigateTo(Destination.ProfileNotifications) } answers { goNotif = true }
    every { navActions.navigateTo(Destination.ProfileSecurityPrivacy) } answers { goSecu = true }
    every { navActions.navigateTo(Destination.ProfileTheme) } answers { goTheme = true }

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
      onNodeWithTag("Theme").performScrollTo().assertIsDisplayed()
      onNodeWithTag("Privacy").performScrollTo().assertIsDisplayed()
      onNodeWithTag("Notifications").performScrollTo().assertIsDisplayed()
      onNodeWithTag("manageMembers").performScrollTo().assertIsDisplayed()
      onNodeWithTag("manageRoles").performScrollTo().assertIsDisplayed()
      onNodeWithTag("logoutButton").performScrollTo().assertIsDisplayed()
      onNodeWithTag("associationDropdown").performScrollTo().performClick()
      onAllNodesWithTag("associationDropdownItem").assertCountEquals(3) // depends on listAsso
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
      onNodeWithTag("editName").performClick().performTextInput("newName")
      onNodeWithTag("confirmModifyButton").performClick() // need to set action in test
      assert(mViewmodel.uiState.value.myName == "newName")
    }
  }

  @Test
  fun navigateToSubScreen() {
    with(composeTestRule) {
      onNodeWithTag("Theme").performClick()
      assert(goTheme)

      onNodeWithTag("Privacy").performClick()
      assert(goSecu)

      onNodeWithTag("Notifications").performClick()
      assert(goNotif)
    }
  }

  @Test
  fun navigate() {
    with(composeTestRule) {
      onNodeWithTag("mainNavBarItem/treasury").performClick()
      assert(tabSelected)
    }
  }
}
