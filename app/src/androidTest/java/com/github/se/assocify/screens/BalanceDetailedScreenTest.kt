package com.github.se.assocify.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.BalanceAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.database.ReceiptAPI
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BalanceItem
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.model.entities.Status
import com.github.se.assocify.model.entities.TVA
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.balance.BalanceDetailedScreen
import com.github.se.assocify.ui.screens.treasury.accounting.balance.BalanceDetailedViewModel
import com.github.se.assocify.ui.screens.treasury.accounting.budget.BudgetDetailedViewModel
import com.github.se.assocify.ui.util.PriceUtil
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BalanceDetailedScreenTest :
    TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockBudgetAPI: BudgetAPI
  val subCategoryUid = "subCategoryUid"
  val categoryList =
      listOf(
          AccountingCategory("1", "Events"),
          AccountingCategory("2", "Pole"),
          AccountingCategory("3", "Commissions"),
          AccountingCategory("4", "Sponsorship"))
  val subCategoryList =
      listOf(
          AccountingSubCategory(subCategoryUid, "2", "Logistics", 1205, 2023),
          AccountingSubCategory("2", "categoryUid", "Administration", 100, 2023),
          AccountingSubCategory("3", "categoryUid", "Balelec", 399, 2023))

  val balanceItems =
      listOf(
          BalanceItem(
              "1",
              "pair of scissors",
              subCategoryUid,
              "00000000-0000-0000-0000-000000000000",
              5,
              TVA.TVA_8,
              "scissors for paper cutting",
              LocalDate.of(2023, 4, 14),
              "François Théron",
              Status.Pending),
          BalanceItem(
              "2",
              "sweaters",
              subCategoryUid,
              "00000000-0000-0000-0000-000000000000",
              1000,
              TVA.TVA_8,
              "order for 1000 sweaters",
              LocalDate.of(2023, 3, 11),
              "Rayan Boucheny",
              Status.Archived),
          BalanceItem(
              "3",
              "chairs",
              subCategoryUid,
              "00000000-0000-0000-0000-000000000000",
              200,
              TVA.TVA_8,
              "order for 200 chairs",
              LocalDate.of(2023, 1, 14),
              "Sidonie Bouthors",
              Status.Reimbursed))

  private val receiptList =
      listOf<Receipt>(
          Receipt(
              "00000000-0000-0000-0000-000000000000",
              "r1",
              "a cool receipt",
              LocalDate.of(2023, 3, 11),
              28,
              Status.Pending,
              null,
              "userId"),
          Receipt(
              "00000000-0000-0000-0000-000000000000",
              "r2",
              "a bad receipt",
              LocalDate.of(2023, 3, 11),
              28,
              Status.Approved,
              null,
              "userId"))

  val mockBalanceAPI: BalanceAPI =
      mockk<BalanceAPI>() {
        every { getBalance(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(List<BalanceItem>) -> Unit>()
              onSuccessCallback(balanceItems)
              balanceItems
            }
        every { deleteBalance(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<() -> Unit>()
              onSuccessCallback()
            }
        every { updateBalance(any(), any(), any(), any(), any(), any()) } answers
            {
              val onSuccessCallback = arg<() -> Unit>(4)
              onSuccessCallback()
            }
        every { addBalance(any(), any(), any(), any(), any(), any()) } answers
            {
              val onSuccessCallback = arg<() -> Unit>(4)
              onSuccessCallback()
            }
      }

  val mockAccountingSubCategoryAPI: AccountingSubCategoryAPI =
      mockk<AccountingSubCategoryAPI>() {
        every { getSubCategories(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(List<AccountingSubCategory>) -> Unit>()
              onSuccessCallback(subCategoryList)
            }
        every { updateSubCategory(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<() -> Unit>()
              onSuccessCallback()
            }
        every { deleteSubCategory(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<() -> Unit>()
              onSuccessCallback()
            }
      }

  val mockAccountingCategoryAPI: AccountingCategoryAPI =
      mockk<AccountingCategoryAPI>() {
        every { updateCategory(any(), any(), any(), any()) } answers {}
        every { getCategories(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(List<AccountingCategory>) -> Unit>()
              onSuccessCallback(categoryList)
            }
      }

  val mockReceiptAPI: ReceiptAPI =
      mockk<ReceiptAPI>() {
        every { getAllReceipts(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(List<Receipt>) -> Unit>()
              onSuccessCallback(receiptList)
            }
      }

  lateinit var budgetDetailedViewModel: BudgetDetailedViewModel
  lateinit var balanceDetailedViewModel: BalanceDetailedViewModel

  @Before
  fun setup() {
    CurrentUser.userUid = "userId"
    CurrentUser.associationUid = "associationId"
    budgetDetailedViewModel =
        BudgetDetailedViewModel(
            mockNavActions,
            mockBudgetAPI,
            mockAccountingSubCategoryAPI,
            mockAccountingCategoryAPI,
            subCategoryUid)
    balanceDetailedViewModel =
        BalanceDetailedViewModel(
            mockNavActions,
            mockBalanceAPI,
            mockReceiptAPI,
            mockAccountingSubCategoryAPI,
            mockAccountingCategoryAPI,
            subCategoryUid)
    composeTestRule.setContent {
      BalanceDetailedScreen(mockNavActions, budgetDetailedViewModel, balanceDetailedViewModel)
    }
  }

  /** Tests if the nodes are displayed */
  @Test
  fun testDisplay() {
    // Test the accounting screen
    with(composeTestRule) {
      onNodeWithTag("AccountingDetailedScreen").assertIsDisplayed()
      onNodeWithTag("filterRowDetailed").assertIsDisplayed()
      onNodeWithTag("totalItems").assertIsDisplayed()
      onNodeWithTag("statusListTag").assertIsDisplayed()
      onNodeWithTag("tvaListTag").assertIsDisplayed()
    }
  }

  /** Tests message shown when empty list */
  @Test
  fun testEmptyList() {
    with(composeTestRule) {
      onNodeWithTag("statusListTag").performClick()
      onNodeWithText("Approved").performClick()
      onNodeWithTag("totalItems").assertIsNotDisplayed()
      onNodeWithText("No items for the ${subCategoryList.first().name} sheet with these filters")
          .assertIsDisplayed()
    }
  }

  /** Tests if the items of 2023 are displayed (the default) */
  @Test
  fun testCorrectItemsAreDisplayed() {
    with(composeTestRule) {
      onNodeWithText("sweaters").assertIsDisplayed()
      onNodeWithText("chairs").assertIsDisplayed()
      onNodeWithText("pair of scissors").assertIsDisplayed()
      // Assert that the name of the subCategory is displayed
      onNodeWithText("Logistics").assertIsDisplayed()
    }

    assert(
        balanceItems.filter { it.subcategoryUID == subCategoryUid } ==
            balanceDetailedViewModel.uiState.value.balanceList)
  }

  /** Tests if the total amount correspond to the sum of the items */
  @Test
  fun testTotalAmount() {
    // Test the accounting screen
    with(composeTestRule) {
      onNodeWithTag("totalItems").assertIsDisplayed()
      var total = 0
      balanceItems.forEach { total += it.amount }
      onNodeWithText(total.toString())
    }
  }

  /** Tests if go back to Treasury */
  @Test
  fun goBackTest() {
    with(composeTestRule) {
      onNodeWithTag("backButton").performClick()
      verify { mockNavActions.back() }
    }
  }

  /** Tests if the lines are filtered according to the status */
  @Test
  fun testStatusFiltering() {
    with(composeTestRule) {
      // Initially, select the "Status" filter to change its value to Pending and 2022
      onNodeWithTag("statusListTag").performClick()
      onNodeWithText("Pending").performClick()

      // Assert that only the item "pair of scissors" is displayed
      onNodeWithText("pair of scissors").assertIsDisplayed()
      assert(
          balanceItems.filter { it.status == Status.Pending } ==
              balanceDetailedViewModel.uiState.value.balanceList)

      // Change the status filter to "All Status"
      onNodeWithTag("statusListTag").performClick()
      onNodeWithText("All Status").performClick()

      // Assert that all items of 2022 are displayed
      assert(
          balanceItems.filter { it.subcategoryUID == subCategoryUid } ==
              balanceDetailedViewModel.uiState.value.balanceList)
      assert(null == balanceDetailedViewModel.uiState.value.status)
    }
  }

  /** Tests if filter row is scrollable */
  @Test
  fun testsIfFilterRowIsScrollable() {
    with(composeTestRule) {
      onNodeWithTag("filterRowDetailed").assertIsDisplayed()
      onNodeWithTag("filterRowDetailed").performTouchInput { swipeLeft() }
    }
  }

  /** Tests if the subCategory edit pop up is displayed and working correctly */
  @Test
  fun testSubCatEditPopUp() {
    with(composeTestRule) {
      onNodeWithTag("editSubCat").performClick()
      assert(balanceDetailedViewModel.uiState.value.subCatEditing)
      onNodeWithTag("editSubCategoryDialog").assertIsDisplayed()
      onNodeWithTag("editSubCategoryNameBox").assertIsDisplayed()
      onNodeWithTag("editSubCategoryNameBox").performTextClearance()
      onNodeWithTag("editSubCategoryNameBox").performTextInput("newName")
      onNodeWithTag("editSubCategoryYearBox").assertIsDisplayed()
      onNodeWithTag("editSubCategoryYearBox").performTextClearance()
      onNodeWithTag("editSubCategoryYearBox").performTextInput("2024")
      onNodeWithTag("categoryDropdown").assertIsDisplayed()
      onNodeWithTag("categoryDropdown").performClick()
      onNodeWithText("Events").performClick()
      onNodeWithTag("editSubCategorySaveButton").performClick()
      onNodeWithTag("editSubCategoryDialog").assertIsNotDisplayed()
      assert(!balanceDetailedViewModel.uiState.value.subCatEditing)
      onNodeWithText("newName").assertIsDisplayed()
      assert(balanceDetailedViewModel.uiState.value.subCategory!!.name == "newName")
      assert(balanceDetailedViewModel.uiState.value.subCategory!!.year == 2024)
      assert(balanceDetailedViewModel.uiState.value.subCategory!!.categoryUID == "1")
    }
  }

  /** Tests if the subCategory edit pop up is canceled correctly */
  @Test
  fun testSubCatEditCancel() {
    with(composeTestRule) {
      onNodeWithTag("editSubCat").performClick()
      assert(balanceDetailedViewModel.uiState.value.subCatEditing)
      onNodeWithTag("editSubCategoryDialog").assertIsDisplayed()
      onNodeWithTag("editSubCategoryNameBox").assertIsDisplayed()
      onNodeWithTag("editSubCategoryNameBox").performTextClearance()
      onNodeWithTag("editSubCategoryNameBox").performTextInput("newName")
      onNodeWithTag("editSubCategoryYearBox").assertIsDisplayed()
      onNodeWithTag("editSubCategoryYearBox").performTextClearance()
      onNodeWithTag("editSubCategoryYearBox").performTextInput("2024")
      onNodeWithTag("categoryDropdown").performClick()
      onNodeWithText("Sponsorship").performClick()
      onNodeWithTag("editSubCategoryCancelButton").performClick()
      onNodeWithTag("editSubCategoryDialog").assertIsNotDisplayed()
      assert(!balanceDetailedViewModel.uiState.value.subCatEditing)
      onNodeWithText("newName").assertIsNotDisplayed()
      assert(balanceDetailedViewModel.uiState.value.subCategory!!.name == "Logistics")
      assert(balanceDetailedViewModel.uiState.value.subCategory!!.year == 2023)
      assert(balanceDetailedViewModel.uiState.value.subCategory!!.categoryUID == "2")
    }
  }

  @Test
  fun testDeleteSubCategory() {
    with(composeTestRule) {
      onNodeWithTag("editSubCat").performClick()
      assert(balanceDetailedViewModel.uiState.value.subCatEditing)
      onNodeWithTag("editSubCategoryDialog").assertIsDisplayed()
      onNodeWithTag("editSubCategoryDeleteButton").performClick()
      verify { mockNavActions.back() }
    }
  }

  @Test
  fun tvaFilterWorks() {
    with(composeTestRule) {
      onNodeWithTag("tvaListTag").performClick()
      onNodeWithText("TTC").performClick()
      val totalAmountTTC =
          PriceUtil.fromCents(
              balanceItems.sumOf { (it.amount + it.amount * it.tva.rate / 100f).toInt() })
      onNodeWithText(totalAmountTTC).assertIsDisplayed()
      onNodeWithTag("tvaListTag").performClick()
      onNodeWithText("HT").performClick()
      val totalAmount = PriceUtil.fromCents(balanceItems.sumOf { it.amount })
      onNodeWithText(totalAmount).assertIsDisplayed()
    }
  }

  @Test
  fun testLoadSubCategoryError() {
    val errorMessage = "error"
    val error = Exception(errorMessage)
    every { mockAccountingSubCategoryAPI.getSubCategories(any(), any(), any()) } answers
        {
          val onErrorCallback = thirdArg<(Exception) -> Unit>()
          onErrorCallback(error)
        }
    with(composeTestRule) {
      balanceDetailedViewModel.loadBalanceDetails()
      onNodeWithTag("errorMessage").assertIsDisplayed().assertTextContains("Error loading category")
    }
  }

  @Test
  fun testLoadBalanceError() {
    val errorMessage = "error"
    val error = Exception(errorMessage)
    every { mockBalanceAPI.getBalance(any(), any(), any()) } answers
        {
          val onErrorCallback = thirdArg<(Exception) -> Unit>()
          onErrorCallback(error)
        }
    with(composeTestRule) {
      balanceDetailedViewModel.loadBalanceDetails()
      onNodeWithTag("errorMessage")
          .assertIsDisplayed()
          .assertTextContains("Error loading balance items")
    }
  }

  @Test
  fun testLoadCategoriesError() {
    val errorMessage = "error"
    val error = Exception(errorMessage)
    every { mockAccountingCategoryAPI.getCategories(any(), any(), any()) } answers
        {
          val onErrorCallback = thirdArg<(Exception) -> Unit>()
          onErrorCallback(error)
        }
    with(composeTestRule) {
      balanceDetailedViewModel.loadBalanceDetails()
      onNodeWithTag("errorMessage").assertIsDisplayed().assertTextContains("Error loading tags")
    }
  }

  @Test
  fun testSaveSubCategoryError() {
    val errorMessage = "error"
    val error = Exception(errorMessage)
    every { mockAccountingSubCategoryAPI.updateSubCategory(any(), any(), any()) } answers
        {
          val onErrorCallback = thirdArg<(Exception) -> Unit>()
          onErrorCallback(error)
        }
    with(composeTestRule) {
      onNodeWithTag("editSubCat").performClick()
      onNodeWithTag("editSubCategoryNameBox").assertIsDisplayed()
      onNodeWithTag("editSubCategoryNameBox").performTextClearance()
      onNodeWithTag("editSubCategoryNameBox").performTextInput("newName")
      onNodeWithTag("editSubCategorySaveButton").performClick()
      onNodeWithText("Failed to update category").assertIsDisplayed()
    }
  }

  @Test
  fun testDeleteSubCategoryError() {
    val errorMessage = "error"
    val error = Exception(errorMessage)
    every { mockAccountingSubCategoryAPI.deleteSubCategory(any(), any(), any()) } answers
        {
          val onErrorCallback = thirdArg<(Exception) -> Unit>()
          onErrorCallback(error)
        }
    with(composeTestRule) {
      onNodeWithTag("editSubCat").performClick()
      onNodeWithTag("editSubCategoryDeleteButton").performClick()
      onNodeWithText("Failed to delete category").assertIsDisplayed()
    }
  }

  @Test
  fun testEditDeleteScreen() {
    with(composeTestRule) {
      onNodeWithTag("statusListTag").performClick()
      onNodeWithText("Pending").performClick()

      // Assert that only the item "pair of scissors" is displayed
      onNodeWithText("pair of scissors").assertIsDisplayed()
      onNodeWithText("François Théron").assertIsDisplayed()
      onNodeWithText("pair of scissors").performClick()
      onNodeWithTag("editDialogColumn").performScrollToNode(hasTestTag("editDeleteButton"))
      onNodeWithTag("editDeleteButton").performClick()
      onNodeWithText("pair of scissors").assertIsNotDisplayed()
    }
  }

  @Test
  fun testEditModifyScreen() {
    with(composeTestRule) {
      onNodeWithTag("statusListTag").performClick()
      onNodeWithText("Pending").performClick()

      // Assert that only the item "pair of scissors" is displayed
      onNodeWithText("pair of scissors").assertIsDisplayed()
      onNodeWithText("François Théron").assertIsDisplayed()
      onNodeWithText("pair of scissors").performClick()
      onNodeWithTag("editDialogName").assertIsDisplayed()
      onNodeWithTag("editDialogName").performTextClearance()
      onNodeWithTag("editDialogName").performTextInput("money")
      onNodeWithTag("editDialogColumn").performScrollToNode(hasTestTag("editConfirmButton"))
      onNodeWithTag("editConfirmButton").performClick()
      onNodeWithText("money").assertIsDisplayed()
      onNodeWithText("pair of scissors").assertIsNotDisplayed()
    }
  }

  @Test
  fun testCancelModifyScreen() {
    with(composeTestRule) {
      onNodeWithTag("statusListTag").performClick()
      onNodeWithText("Pending").performClick()

      // Assert that only the item "pair of scissors" is displayed
      onNodeWithText("pair of scissors").assertIsDisplayed()
      onNodeWithText("François Théron").assertIsDisplayed()
      onNodeWithText("pair of scissors").performClick()
      onNodeWithTag("editDialogName").assertIsDisplayed()
      onNodeWithTag("editDialogName").performTextClearance()
      onNodeWithTag("editDialogName").performTextInput("money")
      onNodeWithTag("editSubCategoryCancelButton").performClick()
      onNodeWithText("money").assertIsNotDisplayed()
      onNodeWithText("pair of scissors").assertIsDisplayed()
    }
  }

  @Test
  fun testCancelCreateScreen() {
    with(composeTestRule) {
      onNodeWithTag("createNewItem").performClick()
      onNodeWithTag("editDialogName").assertIsDisplayed()
      onNodeWithTag("editDialogName").performTextClearance()
      onNodeWithTag("editDialogName").performTextInput("money")
      onNodeWithTag("editSubCategoryCancelButton").performClick()
      onNodeWithText("money").assertIsNotDisplayed()
    }
  }

  @Test
  fun testSaveCreateScreen() {
    with(composeTestRule) {
      onNodeWithTag("createNewItem").performClick()
      onNodeWithTag("editDialogName").assertIsDisplayed()
      onNodeWithTag("editDialogName").performTextClearance()
      onNodeWithTag("editDialogName").performTextInput("lots of money")
      onNodeWithTag("receiptDropdown").assertIsDisplayed()
      onNodeWithTag("receiptDropdown").performClick()
      onNodeWithText("r1").performClick()
      onNodeWithTag("editDialogColumn").performScrollToNode(hasTestTag("editDialogAssignee"))
      onNodeWithTag("editDialogAssignee").performTextClearance()
      onNodeWithTag("editDialogAssignee").performTextInput("François Théron")
      onNodeWithTag("editDialogDate").performClick()
      onNodeWithContentDescription("Switch to text input mode").performClick()
      onNodeWithContentDescription("Date", true).performClick().performTextInput("01012023")
      onNodeWithText("OK").performClick()
      onNodeWithTag("editDialogColumn").performScrollToNode(hasTestTag("editConfirmButton"))
      onNodeWithTag("editConfirmButton").performClick()
      onNodeWithTag("editDialogName").assertIsNotDisplayed()
      onNodeWithText("lots of money").assertIsDisplayed()
    }
  }
}
