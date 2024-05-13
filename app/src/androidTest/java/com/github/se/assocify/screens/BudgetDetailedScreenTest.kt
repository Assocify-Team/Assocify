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
import com.github.se.assocify.model.database.BudgetAPI
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

  @RelaxedMockK lateinit var balanceDetailedViewModel: BalanceDetailedViewModel

  val subCategory = AccountingSubCategory("subCategoryUid", "categoryUid", "Logistics Pole", 1205)
  val budgetItems =
      listOf(
          BudgetItem(
              "1",
              "pair of scissors",
              5,
              TVA.TVA_8,
              "scissors for paper cutting",
              subCategory,
              2022),
          BudgetItem(
              "2", "sweaters", 1000, TVA.TVA_8, "order for 1000 sweaters", subCategory, 2023),
          BudgetItem("3", "chairs", 200, TVA.TVA_8, "order for 200 chairs", subCategory, 2023))

  val mockBudgetAPI: BudgetAPI =
      mockk<BudgetAPI>() {
        every { getBudget(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(List<BudgetItem>) -> Unit>()
              onSuccessCallback(budgetItems)
            }
        every { updateBudgetItem(any(), any(), any(), any()) } answers {}
      }

  lateinit var budgetDetailedViewModel: BudgetDetailedViewModel

  @Before
  fun setup() {
    CurrentUser.userUid = "userId"
    CurrentUser.associationUid = "associationId"
    budgetDetailedViewModel = BudgetDetailedViewModel(mockBudgetAPI, "subCategoryUid")
    composeTestRule.setContent {
      BudgetDetailedScreen(
          "subCategoryUid", mockNavActions, budgetDetailedViewModel, balanceDetailedViewModel)
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
    }

    assert(
        budgetItems.filter { it.year == 2023 } == budgetDetailedViewModel.uiState.value.budgetList)
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
}
