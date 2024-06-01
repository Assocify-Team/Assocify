package com.github.se.assocify.screens.profile

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelectable
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.Theme
import com.github.se.assocify.model.localsave.LocalSave
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.profile.preferences.ProfilePreferencesScreen
import com.github.se.assocify.ui.theme.ThemeViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfilePreferencesScreenTest :
    TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private val navActions = mockk<NavigationActions>()
  private var goBack = false
  private var themeVM = mockk<ThemeViewModel>()
  private val localSave = mockk<LocalSave>()

  @Before
  fun testSetup() {
    CurrentUser.userUid = "1"
    CurrentUser.associationUid = "asso"
    every { themeVM.theme } returns MutableStateFlow(Theme.LIGHT)
    every { navActions.back() } answers { goBack = true }

    composeTestRule.setContent {
      ProfilePreferencesScreen(navActions = navActions, themeVM, localSave)
    }
  }

  @Test
  fun display() {
    with(composeTestRule) {
      onNodeWithTag("preferencesScreen").assertIsDisplayed()

      onNodeWithTag("themeTitle").assertIsDisplayed()
      onNodeWithTag("themeSegmentedButtonRow").assertIsDisplayed()
      Theme.entries.forEach {
        onNodeWithText(text = it.name).assertIsDisplayed().assertIsSelectable()
      }
      onNodeWithText(text = Theme.LIGHT.name).assertIsSelected()

      onNodeWithTag("textSize").assertIsDisplayed()
      onNodeWithTag("textSizeSlider").assertIsDisplayed()
      onNodeWithText("15").assertIsDisplayed() // Default value of text size

      onNodeWithText("Language").assertIsDisplayed()
      onNodeWithTag("languageDropdown").assertIsDisplayed()
      // Default value of language, only one implemented for now :
      onNodeWithText("English").assertIsNotEnabled()
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
