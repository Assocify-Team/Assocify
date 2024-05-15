package com.github.se.assocify.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
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
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.entities.TVA
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.balance.BalanceDetailedViewModel
import com.github.se.assocify.ui.screens.treasury.accounting.budget.BudgetDetailedScreen
import com.github.se.assocify.ui.screens.treasury.accounting.budget.BudgetDetailedViewModel
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
              2022),
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
      }

  val mockAccountingSubCategoryAPI: AccountingSubCategoryAPI =
      mockk<AccountingSubCategoryAPI>() {
        every { getSubCategories(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(List<AccountingSubCategory>) -> Unit>()
              onSuccessCallback(subCategoryList)
            }
        every { updateSubCategory(any(), any(), any()) } answers {}
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
            mockBudgetAPI,
            mockBalanceAPI,
            mockAccountingSubCategoryAPI,
            mockAccountingCategoryAPI,
            subCategoryUid)
    balanceDetailedViewModel =
        BalanceDetailedViewModel(
            mockBalanceAPI,
            mockBudgetAPI,
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
      onNodeWithTag("yearListTag").assertIsDisplayed()
      onNodeWithTag("tvaListTag").assertIsDisplayed()
    }
  }

  /** Tests if the items of 2023 are displayed (the default) */
  @Test
  fun testCorrectItemsAreDisplayed() {
    with(composeTestRule) {
      onNodeWithText("sweaters").assertIsDisplayed()
      onNodeWithText("chairs").assertIsDisplayed()
      onNodeWithText("pair of scissors").assertIsNotDisplayed()
      onNodeWithText("fees").assertIsNotDisplayed()
      onNodeWithText(subCategoryList[0].name).assertIsDisplayed()

      assert(
          budgetDetailedViewModel.uiState.value.budgetList ==
              budgetItems.filter { it.year == 2023 && it.subcategoryUID == "subCategoryUid" })
      assert(budgetDetailedViewModel.uiState.value.subCategory == subCategoryList.first())
      assert(budgetDetailedViewModel.uiState.value.yearFilter == 2023)
    }
  }

  /** Tests if amount is not shown when empty list */
  @Test
  fun testEmptyList() {
    with(composeTestRule) {
      onNodeWithTag("yearListTag").performClick()
      onNodeWithText("2021").performClick()
      onNodeWithTag("totalItems").assertIsNotDisplayed()
      onNodeWithText("No items for the ${subCategoryList.first().name} sheet with these filters")
          .assertIsDisplayed()
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

  /** Tests if the nodes of filter year are displayed */
  @Test
  fun testFilterByYear() {
    with(composeTestRule) {
      onNodeWithTag("yearListTag").performClick()
      onNodeWithText("2022").performClick()

      onNodeWithText("sweaters").assertDoesNotExist()
      onNodeWithText("chairs").assertDoesNotExist()

      onNodeWithText("pair of scissors").assertIsDisplayed()
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
      onNodeWithTag("yearListTag").performClick()
      onNodeWithText("2022").performClick()
      onNodeWithText("pair of scissors").performClick()
      onNodeWithTag("editDialogBox").assertIsDisplayed()
      onNodeWithTag("editNameBox").performTextClearance()
      onNodeWithTag("editNameBox").performTextInput("scotch")
      onNodeWithTag("editDismissButton").performClick()
      onNodeWithTag("editDialogBox").assertIsNotDisplayed()
      onNodeWithText("pair of scissors").assertIsDisplayed()
      onNodeWithText("scotch").assertIsNotDisplayed()
    }
  }

  /** Tests if the budget item edit pop up is displayed and working correctly */
  @Test
  fun testEditModifyWorks() {
    with(composeTestRule) {
      onNodeWithTag("yearListTag").performClick()
      onNodeWithText("2022").performClick()
      onNodeWithText("pair of scissors").performClick()
      onNodeWithTag("editDialogBox").assertIsDisplayed()
      onNodeWithTag("editNameBox").performTextClearance()
      onNodeWithTag("editNameBox").performTextInput("scotch")
      onNodeWithTag("editConfirmButton").performClick()
      onNodeWithTag("editDialogBox").assertIsNotDisplayed()
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
      assert(budgetDetailedViewModel.uiState.value.subCategory.name == "newName")
      assert(budgetDetailedViewModel.uiState.value.subCategory.year == 2024)
      assert(balanceDetailedViewModel.uiState.value.subCategory.categoryUID == "1")
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
      assert(budgetDetailedViewModel.uiState.value.subCategory.name == "Logistics")
      assert(budgetDetailedViewModel.uiState.value.subCategory.year == 2023)
      assert(budgetDetailedViewModel.uiState.value.subCategory.categoryUID == "2")
    }
  }
}
