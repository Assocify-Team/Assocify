package com.github.se.assocify

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onSibling
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.entities.Theme
import com.github.se.assocify.ui.theme.AssocifyTheme
import com.github.se.assocify.ui.theme.md_theme_dark_primary
import com.github.se.assocify.ui.theme.md_theme_light_primary
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Composable
fun TestScreen() {
  Text("Test")
  Text((MaterialTheme.colorScheme.primary).toString())
}

@RunWith(AndroidJUnit4::class)
class ThemeTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @Before fun testSetup() {}

  @Test
  fun darkTheme() {

    composeTestRule.setContent {
      AssocifyTheme(theme = Theme.DARK, dynamicColor = false) { TestScreen() }
    }
    with(composeTestRule) {
      onNodeWithText("Test")
          .assertIsDisplayed()
          .onSibling()
          .assertTextContains(md_theme_dark_primary.toString())
    }
  }

  @Test
  fun lightTheme() {
    composeTestRule.setContent {
      AssocifyTheme(theme = Theme.LIGHT, dynamicColor = false) { TestScreen() }
    }
    with(composeTestRule) {
      onNodeWithText("Test")
          .assertIsDisplayed()
          .onSibling()
          .assertTextContains(md_theme_light_primary.toString())
    }
  }

  @Test
  fun systemTheme() {
    composeTestRule.setContent {
      AssocifyTheme(theme = Theme.SYSTEM, dynamicColor = false) { TestScreen() }
    }
    with(composeTestRule) {
      onNodeWithText("Test")
          .assertIsDisplayed()
          .onSibling()
          .assertTextContains(md_theme_light_primary.toString())
    }
  }
}
