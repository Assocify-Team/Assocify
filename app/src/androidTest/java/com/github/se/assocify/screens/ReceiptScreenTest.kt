package com.github.se.assocify.screens

import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.database.ReceiptsAPI
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.receipt.ReceiptScreen
import com.github.se.assocify.ui.screens.treasury.receipt.ReceiptViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReceiptScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private val navActions = mockk<NavigationActions>(relaxUnitFun = true)
  private val receiptsAPI = mockk<ReceiptsAPI>(relaxUnitFun = true)
  private val viewModel = ReceiptViewModel(navActions, receiptsAPI)

  /*val userMai = User("1", "Maï", Role())
  val userSeb = User("2", "Sebastien", Role())
  val userSido = User("3", "Sido", Role())
  val userList = listOf(userMai, userSeb, userSido)*/

  @Before
  fun testSetup() {
    composeTestRule.setContent { ReceiptScreen(navActions = navActions, viewModel) }
  }

  @Test
  fun display() {
    with(composeTestRule) {
      Log.e("LMAO", "Huluberlu")

      onNodeWithTag("receiptScreen").assertIsDisplayed()
      onNodeWithTag("receiptScreenTitle").assertIsDisplayed()
      onNodeWithTag("backButton").assertIsDisplayed()
      onNodeWithTag("receiptScreen").assertIsDisplayed()
      onNodeWithTag("titleField").assertIsDisplayed()
      onNodeWithTag("descriptionField").assertIsDisplayed()
      onNodeWithTag("amountField").performScrollTo().assertIsDisplayed()
      // onNodeWithTag("payerField").performScrollTo().assertIsDisplayed()
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
      onNodeWithTag("titleField").performClick().performTextInput("Test Title")
      assert(viewModel.uiState.value.title == "Test Title")
      onNodeWithTag("titleField").assertTextContains("Test Title")

      onNodeWithTag("titleField").performClick().performTextClearance()
      assert(viewModel.uiState.value.title == "")
      onNodeWithTag("titleField").assertTextContains("Title cannot be empty")
    }
  }

  @Test
  fun description() {
    with(composeTestRule) {
      onNodeWithTag("descriptionField").performClick().performTextInput("Test Description")
      assert(viewModel.uiState.value.description == "Test Description")
      onNodeWithTag("descriptionField").assertTextContains("Test Description")
    }
  }

  @Test
  fun amount() {
    with(composeTestRule) {
      onNodeWithTag("amountField").performScrollTo().performClick().performTextInput("100")
      assert(viewModel.uiState.value.amount == "100")
      onNodeWithTag("amountField").assertTextContains("100")

      onNodeWithTag("amountField").performClick().performTextClearance()
      assert(viewModel.uiState.value.amount == "")
      onNodeWithTag("amountField").assertTextContains("Price cannot be empty")

      onNodeWithTag("amountField").performClick().performTextInput("0")
      assert(viewModel.uiState.value.amount == "0")
      onNodeWithTag("amountField").assertTextContains("Price cannot be zero")

      onNodeWithTag("amountField").performClick().performTextClearance()
      onNodeWithTag("amountField").performTextInput(".")
      assert(viewModel.uiState.value.amount == ".")
      onNodeWithTag("amountField").assertTextContains("Price cannot be zero")
      onNodeWithTag("amountField").performTextInput(".") // try to add a second .
      assert(viewModel.uiState.value.amount == ".")

      onNodeWithTag("amountField").performClick().performTextClearance()
      onNodeWithTag("amountField").performTextInput("1000000.00")
      onNodeWithTag("amountField").performTextInput("1") // try to add another 1 at end
      assert(viewModel.uiState.value.amount == "1000000.00")
      onNodeWithTag("amountField").assertTextContains("Price is too large")
    }
  }

  /*@Test
  fun payer() {
    with(composeTestRule) {
      // Test search : should work without capital letter,
      onNodeWithTag("payerField").performClick().performTextInput("maï")
      onNodeWithTag("userDropdown").assertIsDisplayed()
      onNodeWithTag("userDropdownItem-1").assertIsDisplayed().performClick()
      assert(viewModel.uiState.value.payer == userMai)
      onNodeWithTag("payerField").assertTextContains("Maï")

      onNodeWithTag("userDismissButton").assertIsDisplayed().performClick()
      assert(viewModel.uiState.value.payerSearch == "")

      onNodeWithTag("payerField").performClick().performTextInput("Ya")
      onNodeWithTag("payerField").assertTextContains("No users found")

      onNodeWithTag("payerField").performClick().performTextClearance()
      onNodeWithTag("payerField").performClick().performTextInput("S")
      onNodeWithTag("userDropdown").assertIsDisplayed().onChildren().assertCountEquals(2)
    }
  }*/

  @Test
  fun save() {
    with(composeTestRule) { onNodeWithTag("saveButton").performScrollTo().performClick() }
    /* TODO: verify { viewModel.save() } */
  }

  @Test
  fun back() {
    with(composeTestRule) {
      onNodeWithTag("backButton").performClick()
      verify { navActions.back() }
    }
  }

  @Test
  fun datePicker() {
    with(composeTestRule) {
      onNodeWithTag("dateField").performClick()
      onNodeWithTag("datePickerDialog").assertIsDisplayed()
      onNodeWithTag("datePickerDialogCancel").performClick()
      onNodeWithTag("datePickerDialog").assertDoesNotExist()
      onNodeWithTag("dateField").assertTextContains("Date cannot be empty")
    }
  }
}
