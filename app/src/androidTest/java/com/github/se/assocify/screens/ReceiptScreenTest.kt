package com.github.se.assocify.screens

import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.ReceiptAPI
import com.github.se.assocify.model.entities.MaybeRemotePhoto
import com.github.se.assocify.model.entities.Status
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.receipt.ReceiptScreen
import com.github.se.assocify.ui.screens.treasury.receipt.ReceiptViewModel
import com.github.se.assocify.ui.util.DateUtil
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReceiptScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private var testUri = Uri.parse("content://test")

  private var capturedReceipt: Receipt? = null
  private var expectedReceipt =
      Receipt(
          uid = "testReceipt",
          title = "Test Title",
          description = "",
          cents = 10000,
          date = DateUtil.toDate("01/01/2021")!!,
          incoming = false,
          status = Status.Unapproved,
          photo = MaybeRemotePhoto.LocalFile(testUri),
      )

  private val navActions = mockk<NavigationActions>(relaxUnitFun = true)
  private val receiptAPI =
      mockk<ReceiptAPI>() {
        every { uploadReceipt(any(), any(), any(), any()) } answers
            {
              capturedReceipt = firstArg<Receipt>()
              navActions.back()
            }
        every { getNewId() } returns "testReceipt"
        every { deleteReceipt(any(), any(), any()) } answers {}
      }
  private val viewModel = ReceiptViewModel(navActions = navActions, receiptApi = receiptAPI)

  @Before
  fun testSetup() {
    CurrentUser.userUid = "testUser"
    CurrentUser.associationUid = "testUser"
    composeTestRule.setContent { ReceiptScreen(navActions = navActions, viewModel = viewModel) }
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

  @Test
  fun incoming() {
    with(composeTestRule) {
      onNodeWithTag("earningChip").performScrollTo().performClick()
      assert(viewModel.uiState.value.incoming)

      onNodeWithTag("expenseChip").performScrollTo().performClick()
      assert(!viewModel.uiState.value.incoming)
    }
  }

  @Test
  fun save() {
    with(composeTestRule) {
      onNodeWithTag("saveButton").performScrollTo().performClick()
      onNodeWithTag("titleField").assertTextContains("Title cannot be empty")
      onNodeWithTag("amountField").assertTextContains("Price cannot be empty")
      onNodeWithTag("dateField").assertTextContains("Date cannot be empty")

      onNodeWithTag("titleField").performClick().performTextInput("Test Title")

      onNodeWithTag("amountField").performClick().performTextInput("100")

      onNodeWithTag("dateField").performClick()
      onNodeWithContentDescription("Switch to text input mode").performClick()
      onNodeWithContentDescription("Date", true).performClick().performTextInput("01012021")
      onNodeWithTag("datePickerDialogOk").performClick()
      onNodeWithTag("dateField").assertTextContains("01/01/2021")

      assert(viewModel.uiState.value.titleError == null)
      assert(viewModel.uiState.value.amountError == null)
      assert(viewModel.uiState.value.dateError == null)

      onNodeWithTag("saveButton").performScrollTo().performClick()
      onNodeWithText("Receipt image is required", true).assertIsDisplayed()

      viewModel.setImage(testUri)
      assert(viewModel.uiState.value.receiptImageURI != null)

      viewModel.saveReceipt()
      verify { receiptAPI.uploadReceipt(any(), any(), any(), any()) }
      assert(capturedReceipt == expectedReceipt)
    }
  }

  @Test
  fun delete() {
    with(composeTestRule) {
      onNodeWithTag("deleteButton").performScrollTo().performClick()
      verify { navActions.back() }
    }
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

  @Test
  fun cameraPermission() {
    with(composeTestRule) {
      viewModel.signalCameraPermissionDenied()
      onNodeWithText("Camera permission denied", true).assertIsDisplayed()
    }
  }

  @Test
  fun photoSheet() {
    with(composeTestRule) {
      onNodeWithTag("editImageButton").performClick()
      onNodeWithTag("photoSelectionSheet").assertIsDisplayed()
      viewModel.hideBottomSheet()
      onNodeWithTag("photoSelectionSheet").assertDoesNotExist()
    }
  }
}

@RunWith(AndroidJUnit4::class)
class EditReceiptScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private var testUri = Uri.parse("content://test")

  private var expectedReceipt =
      Receipt(
          uid = "testReceipt",
          title = "Edited Receipt",
          description = "",
          cents = 10000,
          date = DateUtil.toDate("01/01/2021")!!,
          incoming = false,
          status = Status.Unapproved,
          photo = null,
      )

  private var receiptList = listOf(expectedReceipt)

  private var capturedReceipt: Receipt? = null

  private val navActions = mockk<NavigationActions>(relaxUnitFun = true)
  private val receiptsAPI =
      mockk<ReceiptAPI> {
        every { uploadReceipt(any(), any(), any(), any()) } answers
            {
              capturedReceipt = firstArg()
              navActions.back()
            }
        every { getUserReceipts(any(), any()) } answers
            {
              firstArg<(List<Receipt>) -> Unit>().invoke(receiptList)
            }
        every { getNewId() } answers { "testReceipt" }
      }
  private val viewModel =
      ReceiptViewModel(
          receiptUid = "testReceipt", navActions = navActions, receiptApi = receiptsAPI)

  @Before
  fun testSetup() {
    CurrentUser.userUid = "testUser"
    CurrentUser.associationUid = "testUser"
    composeTestRule.setContent {
      ReceiptScreen(receiptUid = "testReceipt", navActions = navActions, viewModel = viewModel)
    }
  }

  @Test
  fun editReceipt() {
    with(composeTestRule) {
      onNodeWithTag("receiptScreen").assertIsDisplayed()
      onNodeWithTag("receiptScreenTitle").assertIsDisplayed().assertTextContains("Edit Receipt")
      verify { receiptsAPI.getUserReceipts(any(), any()) }
      onNodeWithTag("titleField").assertTextContains("Edited Receipt")
      onNodeWithTag("amountField").assertTextContains("100.00")
      onNodeWithTag("dateField").assertTextContains("01/01/2021")

      viewModel.setImage(testUri)
      assert(viewModel.uiState.value.receiptImageURI != null)

      onNodeWithTag("saveButton").performScrollTo().performClick()
      assert(capturedReceipt?.title == expectedReceipt.title)
      assert(capturedReceipt?.cents == expectedReceipt.cents)
    }
  }
}
