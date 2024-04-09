package com.github.se.assocify.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.receipt.ReceiptScreen
import com.github.se.assocify.ui.screens.treasury.receipt.ReceiptViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReceiptScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private val navActions = mockk<NavigationActions>()
  private val viewModel = spyk<ReceiptViewModel>()
  private var tabSelected = false
  private var saved = false

  @Before
  fun testSetup() {
    every { navActions.back() } answers { tabSelected = true }
    every { viewModel.saveReceipt() } answers { saved = true }
    composeTestRule.setContent { ReceiptScreen(navActions = navActions, viewModel) }
  }

  @Test
  fun display() {
    with(composeTestRule) {
      onNodeWithTag("receiptScreen").assertIsDisplayed()
      onNodeWithTag("receiptScreenTitle").assertIsDisplayed()
      onNodeWithTag("backButton").assertIsDisplayed()
      onNodeWithTag("receiptScreen").assertIsDisplayed()
      onNodeWithTag("titleField").assertIsDisplayed()
      onNodeWithTag("descriptionField").assertIsDisplayed()
      onNodeWithTag("amountField").performScrollTo().assertIsDisplayed()
      onNodeWithTag("payerField").performScrollTo().assertIsDisplayed()
      onNodeWithTag("dateField").performScrollTo().assertIsDisplayed()
      onNodeWithTag("imageCard").performScrollTo().assertIsDisplayed()
      onNodeWithTag("editImageButton").performScrollTo().assertIsDisplayed()
      onNodeWithTag("expenseChip").performScrollTo().assertIsDisplayed()
      onNodeWithTag("earningChip").performScrollTo().assertIsDisplayed()
      onNodeWithTag("saveButton").performScrollTo().assertIsDisplayed()
      onNodeWithTag("deleteButton").performScrollTo().assertIsDisplayed()
    }
  }

  @Test
  fun title() {
    with(composeTestRule) {
      onNodeWithTag("titleField").performClick()
      onNodeWithTag("titleField").performTextInput("Test Title")
      assert(viewModel.uiState.value.title == "Test Title")

      onNodeWithTag("titleField").assertTextContains("Test Title")
    }
  }

  @Test
  fun description() {
    with(composeTestRule) {
      onNodeWithTag("descriptionField").performClick()
      onNodeWithTag("descriptionField").performTextInput("Test Description")
      assert(viewModel.uiState.value.description == "Test Description")
      onNodeWithTag("descriptionField").assertTextContains("Test Description")
    }
  }

  @Test
  fun amount() {
    with(composeTestRule) {
      onNodeWithTag("amountField").performClick()
      onNodeWithTag("amountField").performTextInput("100")
      assert(viewModel.uiState.value.amount == "100")
      onNodeWithTag("amountField").assertTextContains("100")
    }
  }

  @Test
  fun payer() {
    with(composeTestRule) {
      onNodeWithTag("payerField").performClick()
      onNodeWithTag("payerField").performTextInput("Test User")
      assert(viewModel.uiState.value.payer == "Test User")
      onNodeWithTag("payerField").assertTextContains("Test User")
    }
  }

  @Test
  fun save() {
    with(composeTestRule) {
      onNodeWithTag("saveButton").performClick()
      assert(saved)
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
