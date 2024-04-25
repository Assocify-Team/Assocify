package com.github.se.assocify.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.profile.ProfileScreen
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
  private val uid = "1"

  private val navActions = mockk<NavigationActions>(/*relaxUnitFun = true*/ )
  private var tabSelected = false
  private val mockAssocAPI = mockk<AssociationAPI>(relaxUnitFun = true)
  private val mockUserAPI =
      mockk<UserAPI> {
        every { getUser(uid, any(), any()) } answers { User("1", "jean", Role("role")) }
      }

  @Before
  fun testSetup() {
    CurrentUser.userUid = uid
    CurrentUser.associationUid = "asso"
    every { navActions.navigateToMainTab(any()) } answers { tabSelected = true }
    composeTestRule.setContent {
      ProfileScreen(navActions = navActions, assoAPI = mockAssocAPI, userAPI = mockUserAPI)
    }
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
