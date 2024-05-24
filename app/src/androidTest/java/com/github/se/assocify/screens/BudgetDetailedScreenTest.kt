package com.github.se.assocify.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.entities.TVA
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.balance.BalanceDetailedViewModel
import com.github.se.assocify.ui.screens.treasury.accounting.budget.BudgetDetailedScreen
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BudgetDetailedScreenTest :
    TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockBalanceAPI: BalanceAPI
  @RelaxedMockK lateinit var mockReceiptAPI: ReceiptAPI
  val subCategoryUid = "subCategoryUid"
  val subCategoryList =
      listOf(
          AccountingSubCategory(subCategoryUid, "2", "Logistics", 1205, 2023),
          AccountingSubCategory("2", "categoryUid", "Administration", 100, 2023),
          AccountingSubCategory("3", "categoryUid", "Balelec", 399, 2023))
  val categoryList =
      listOf(
          AccountingCategory("1", "Events"),
          AccountingCategory("2", "Pole"),
          AccountingCategory("3", "Commissions"),
          AccountingCategory("4", "Sponsorship"))

  val budgetItems =
      listOf(
          BudgetItem(
              "1",
              "pair of scissors",
              5,
              TVA.TVA_8,
              "scissors for paper cutting",
              subCategoryUid,
              2023),
          BudgetItem(
              "2", "sweaters", 1000, TVA.TVA_8, "order for 1000 sweaters", subCategoryUid, 2023),
          BudgetItem("3", "chairs", 200, TVA.TVA_8, "order for 200 chairs", subCategoryUid, 2023))

  val mockBudgetAPI: BudgetAPI =
      mockk<BudgetAPI>() {
        every { getBudget(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(List<BudgetItem>) -> Unit>()
              onSuccessCallback(budgetItems)
            }
        every { updateBudgetItem(any(), any(), any(), any()) } answers {}
        every { addBudgetItem(any(), any(), any(), any()) } answers
            {
              val onSuccessCallback = thirdArg<() -> Unit>()
              onSuccessCallback()
            }
        every { deleteBudgetItem(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<() -> Unit>()
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
      BudgetDetailedScreen(mockNavActions, budgetDetailedViewModel, balanceDetailedViewModel)
    }
  }

  /** Tests if the nodes are displayed */
  @Test
  fun testDisplay() {
    with(composeTestRule) {
      onNodeWithTag("AccountingDetailedScreen").assertIsDisplayed()
      onNodeWithTag("filterRowDetailed").assertIsDisplayed()
      onNodeWithTag("totalItems").assertIsDisplayed()
      onNodeWithTag("tvaListTag").assertIsDisplayed()
    }
  }

  /** Tests if the items of 2023 are displayed (the default) */
  @Test
  fun testCorrectItemsAreDisplayed() {
    with(composeTestRule) {
      onNodeWithText("sweaters").assertIsDisplayed()
      onNodeWithText("chairs").assertIsDisplayed()
      onNodeWithText("pair of scissors").assertIsDisplayed()
      onNodeWithText(subCategoryList[0].name).assertIsDisplayed()

      assert(
          budgetDetailedViewModel.uiState.value.budgetList ==
              budgetItems.filter { it.subcategoryUID == "subCategoryUid" })
      assert(budgetDetailedViewModel.uiState.value.subCategory == subCategoryList.first())
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

  /** Tests if the total amount correspond to the sum of the items */
  @Test
  fun testTotalAmount() {
    with(composeTestRule) {
      onNodeWithTag("totalItems").assertIsDisplayed()
      var total = 0
      budgetItems.forEach { total += it.amount }
      onNodeWithText(total.toString())
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

  /** Tests if the budget Item pop up is displayed and can cancel */
  @Test
  fun testEditDismissWorks() {
    with(composeTestRule) {
      onNodeWithText("pair of scissors").performClick()
      onNodeWithTag("editDialogBox").assertIsDisplayed()
      onNodeWithTag("editNameBox").performTextClearance()
      onNodeWithTag("editNameBox").performTextInput("scotch")
      onNodeWithTag("editSubCategoryCancelButton").performClick()
      onNodeWithTag("editDialogBox").assertIsNotDisplayed()
      onNodeWithText("pair of scissors").assertIsDisplayed()
      onNodeWithText("scotch").assertIsNotDisplayed()
    }
  }

  /** Tests if the budget item edit pop up is displayed and working correctly */
  @Test
  fun testEditModifyWorks() {
    with(composeTestRule) {
      onNodeWithText("pair of scissors").performClick()
      onNodeWithTag("editDialogBox").assertIsDisplayed()
      onNodeWithTag("editNameBox").performTextClearance()
      onNodeWithTag("editNameBox").performTextInput("scotch")
      onNodeWithTag("editConfirmButton").performClick()
      onNodeWithText("pair of scissors ").assertIsNotDisplayed()
      onNodeWithText("scotch").assertIsDisplayed()
    }
  }

  /** Tests if the subCategory edit pop up is displayed and working correctly */
  @Test
  fun testSubCatEditPopUp() {
    with(composeTestRule) {
      onNodeWithTag("editSubCat").performClick()
      assert(budgetDetailedViewModel.uiState.value.subCatEditing)
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
      assert(!budgetDetailedViewModel.uiState.value.subCatEditing)
      onNodeWithText("newName").assertIsDisplayed()
      assert(budgetDetailedViewModel.uiState.value.subCategory!!.name == "newName")
      assert(budgetDetailedViewModel.uiState.value.subCategory!!.year == 2024)
    }
  }

  /** Tests if the subCategory edit pop up is canceled correctly */
  @Test
  fun testSubCatEditCancel() {
    with(composeTestRule) {
      onNodeWithTag("editSubCat").performClick()
      assert(budgetDetailedViewModel.uiState.value.subCatEditing)
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
      assert(!budgetDetailedViewModel.uiState.value.subCatEditing)
      onNodeWithText("newName").assertIsNotDisplayed()
      assert(budgetDetailedViewModel.uiState.value.subCategory!!.name == "Logistics")
      assert(budgetDetailedViewModel.uiState.value.subCategory!!.year == 2023)
      assert(budgetDetailedViewModel.uiState.value.subCategory!!.categoryUID == "2")
    }
  }

  @Test
  fun testDeleteSubCategory() {
    with(composeTestRule) {
      onNodeWithTag("editSubCat").performClick()
      assert(budgetDetailedViewModel.uiState.value.subCatEditing)
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
              budgetItems.sumOf { (it.amount + it.amount * it.tva.rate / 100f).toInt() })
      onNodeWithText(totalAmountTTC).assertIsDisplayed()
      onNodeWithTag("tvaListTag").performClick()
      onNodeWithText("HT").performClick()
      val totalAmount = PriceUtil.fromCents(budgetItems.sumOf { it.amount })
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
      budgetDetailedViewModel.loadBudgetDetails()
      onNodeWithTag("errorMessage").assertIsDisplayed().assertTextContains("Error loading category")
    }
  }

  @Test
  fun testLoadBudgetError() {
    val errorMessage = "error"
    val error = Exception(errorMessage)
    every { mockBudgetAPI.getBudget(any(), any(), any()) } answers
        {
          val onErrorCallback = thirdArg<(Exception) -> Unit>()
          onErrorCallback(error)
        }
    with(composeTestRule) {
      budgetDetailedViewModel.loadBudgetDetails()
      onNodeWithTag("errorMessage")
          .assertIsDisplayed()
          .assertTextContains("Error loading budget items")
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
      budgetDetailedViewModel.loadBudgetDetails()
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
  fun createTest() {
    with(composeTestRule) {
      onNodeWithTag("createNewItem").performClick()
      onNodeWithTag("editDialogBox").assertIsDisplayed()
      onNodeWithTag("editNameBox").performTextClearance()
      onNodeWithTag("editNameBox").performTextInput("fees")
      onNodeWithTag("editConfirmButton").performClick()
    }
  }

  @Test
  fun deleteTest() {
    with(composeTestRule) {
      onNodeWithText("pair of scissors").assertIsDisplayed()
      onNodeWithText("pair of scissors").performClick()
      onNodeWithTag("deleteButton").performClick()
      onNodeWithText("pair of scissors").assertIsNotDisplayed()
    }
  }
}
