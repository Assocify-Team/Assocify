package com.github.se.assocify.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.entities.TVA
import com.github.se.assocify.navigation.NavigationActions
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

  val subCategoryList =
      listOf(
          AccountingSubCategory("1", "categoryUid", "Logistics", 1205),
          AccountingSubCategory("2", "categoryUid", "Administration", 100),
          AccountingSubCategory("3", "categoryUid", "Balelec", 399)
      )

  val budgetItems =
      listOf(
          BudgetItem(
              "1",
              "pair of scissors",
              5,
              TVA.TVA_8,
              "scissors for paper cutting",
              subCategoryList.first(),
              2022),
          BudgetItem(
              "2", "sweaters", 1000, TVA.TVA_8, "order for 1000 sweaters",
              subCategoryList.first(), 2023),
          BudgetItem("3", "chairs", 200, TVA.TVA_8, "order for 200 chairs",
              subCategoryList.first(), 2023),
          BudgetItem("4", "fees", 300, TVA.TVA_8, "banking fees",
              subCategoryList[1], 2023))

  val mockBudgetAPI: BudgetAPI =
      mockk<BudgetAPI>() {
        every { getBudget(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(List<BudgetItem>) -> Unit>()
              onSuccessCallback(budgetItems)
            }
      }

    val mockAccountingSubCategoryApi: AccountingSubCategoryAPI =
        mockk<AccountingSubCategoryAPI>() {
            every { getSubCategories(any(), any(), any()) } answers
                    {
                        val onSuccessCallback = secondArg<(List<AccountingSubCategory>) -> Unit>()
                        onSuccessCallback(subCategoryList)
                    }
        }

  lateinit var budgetDetailedViewModel: BudgetDetailedViewModel

  @Before
  fun setup() {
    CurrentUser.userUid = "userId"
    CurrentUser.associationUid = "associationId"
    budgetDetailedViewModel = BudgetDetailedViewModel(mockBudgetAPI, mockAccountingSubCategoryApi,"1")
    composeTestRule.setContent {
      BudgetDetailedScreen("1", mockNavActions, budgetDetailedViewModel)
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
        onNodeWithText(subCategoryList.first().name).assertIsDisplayed()

        assert(budgetDetailedViewModel.uiState.value.budgetList == budgetItems.filter { it.year == 2023 &&
            it.category.uid == "1" })
        assert(budgetDetailedViewModel.uiState.value.subCategory == subCategoryList.first())
        assert(budgetDetailedViewModel.uiState.value.yearFilter == 2023)

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
}
