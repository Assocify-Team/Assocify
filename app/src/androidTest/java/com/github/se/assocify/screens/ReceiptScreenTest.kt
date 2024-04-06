package com.github.se.assocify.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.receipt.ReceiptScreen
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
class ReceiptScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private val navActions = mockk<NavigationActions>()
  private var tabSelected = false

  @Before
  fun testSetup() {
    every { navActions.back() } answers { tabSelected = true }
    composeTestRule.setContent { ReceiptScreen(navActions = navActions) }
  }

  @Test
  fun display() {
    with(composeTestRule) {
      onNodeWithTag("receiptScreen").assertIsDisplayed()
      onNodeWithTag("titleField").assertIsDisplayed()
      onNodeWithTag("descriptionField").assertIsDisplayed()
      onNodeWithTag("dateField").assertIsDisplayed()
      onNodeWithTag("backButton").assertIsDisplayed()
    }
  }

  @Test
  fun back() {
    with(composeTestRule) {
      onNodeWithTag("backButton").performClick()
      assert(tabSelected)
    }
  }

  @Test
  fun datePicker() {
    with(composeTestRule) {
      onNodeWithTag("dateField").performClick()
      onNodeWithTag("datePickerDialog").assertIsDisplayed()
      onNodeWithTag("datePickerDialogDismiss").performClick()
    }
  }
}
