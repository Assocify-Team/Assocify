package com.github.se.assocify.screens

import androidx.compose.material3.SnackbarHostState
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
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.BalanceAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BalanceItem
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.entities.Status
import com.github.se.assocify.model.entities.TVA
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingFilterBar
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingPage
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingScreen
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingViewModel
import com.github.se.assocify.ui.util.SnackbarSystem
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import java.lang.Thread.sleep
import java.time.LocalDate
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountingScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  // mocked list
  val categoryList =
      listOf(
          AccountingCategory("1", "Events"),
          AccountingCategory("2", "Pole"),
          AccountingCategory("3", "Commissions"),
          AccountingCategory("4", "Sponsorship"))

  val subCategoryList =
      listOf(
          AccountingSubCategory("4", "2", "Administration", 0, 2022),
          AccountingSubCategory("5", "2", "Presidency", 0, 2023),
          AccountingSubCategory("6", "2", "Communication", 0, 2023),
          AccountingSubCategory("7", "1", "Champachelor", 0, 2022),
          AccountingSubCategory("8", "1", "Balelec", 0, 2022),
          AccountingSubCategory("9", "3", "Game*", 0, 2022),
      )

  val balanceItems =
      listOf(
          BalanceItem(
              "1",
              "pair of scissors",
              "1",
              "00000000-0000-0000-0000-000000000000",
              5,
              TVA.TVA_8,
              "scissors for paper cutting",
              LocalDate.of(2022, 4, 14),
              "François Théron",
              Status.Pending),
          BalanceItem(
              "2",
              "sweaters",
              "2",
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
              "3",
              "00000000-0000-0000-0000-000000000000",
              200,
              TVA.TVA_8,
              "order for 200 chairs",
              LocalDate.of(2023, 1, 14),
              "Sidonie Bouthors",
              Status.Reimbursed))

  val budgetItems =
      listOf(
          BudgetItem(
              "1", "pair of scissors", 5, TVA.TVA_8, "scissors for paper cutting", "4", 2022),
          BudgetItem("2", "sweaters", 1000, TVA.TVA_8, "order for 1000 sweaters", "5", 2023),
          BudgetItem("3", "sweaters 2", 1000, TVA.TVA_2, "order for 10 sweaters", "6", 2023),
          BudgetItem("4", "chairs", 200, TVA.TVA_8, "order for 200 chairs", "3", 2023))

  // mocked APIs
  val mockAccountingCategoryAPI: AccountingCategoryAPI =
      mockk<AccountingCategoryAPI>() {
        every { getCategories(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(List<AccountingCategory>) -> Unit>()
              onSuccessCallback(categoryList)
            }
      }

  val mockAccountingSubCategoryAPI: AccountingSubCategoryAPI =
      mockk<AccountingSubCategoryAPI>() {
        every { getSubCategories(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(List<AccountingSubCategory>) -> Unit>()
              onSuccessCallback(subCategoryList)
            }
      }

  val mockBudgetAPI: BudgetAPI =
      mockk<BudgetAPI>() {
        every { getBudget(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(List<BudgetItem>) -> Unit>()
              onSuccessCallback(budgetItems)
            }
      }

  val mockBalanceAPI: BalanceAPI =
      mockk<BalanceAPI>() {
        every { getBalance(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(List<BalanceItem>) -> Unit>()
              onSuccessCallback(balanceItems)
            }
      }
  lateinit var accountingViewModel: AccountingViewModel

  @Before
  fun setup() {
    CurrentUser.userUid = "userId"
    CurrentUser.associationUid = "associationId"
    accountingViewModel =
        AccountingViewModel(
            mockAccountingCategoryAPI,
            mockAccountingSubCategoryAPI,
            mockBalanceAPI,
            mockBudgetAPI,
            SnackbarSystem(SnackbarHostState()))
    composeTestRule.setContent {
      AccountingScreen(AccountingPage.BUDGET, mockNavActions, accountingViewModel)
      AccountingFilterBar(accountingViewModel = accountingViewModel)
    }
  }

  /** Tests if the nodes are displayed */
  @Test
  fun testDisplay() {
    with(composeTestRule) {
      onNodeWithTag("AccountingScreen").assertIsDisplayed()
      onNodeWithTag("filterRow").assertIsDisplayed()
      onNodeWithTag("totalLine").assertIsNotDisplayed()
      onNodeWithTag("yearFilterChip").assertIsDisplayed()
      onNodeWithTag("categoryFilterChip").assertIsDisplayed()
    }
  }

  /** Tests if the lines are filtered according to the category */
  @Test
  fun testFiltering() {
    with(composeTestRule) {
      onNodeWithTag("yearFilterChip").performClick()
      sleep(2000)
      onNodeWithText("2022").performClick()
      onNodeWithTag("categoryFilterChip").performClick()

      // Tests if the lines are filtered according to the category
      onNodeWithText("Events").performClick()
      subCategoryList
          .filter { it.categoryUID == "1" }
          .filter { it.year == 2022 }
          .forEach() { onNodeWithText(it.name).assertIsDisplayed() }
      assert(!accountingViewModel.uiState.value.globalSelected)
      assert(
          accountingViewModel.uiState.value.subCategoryList ==
              subCategoryList.filter { it.categoryUID == "1" })

      // Tests if the lines are displayed when the global category is selected
      onNodeWithTag("categoryFilterChip").performClick()
      onNodeWithText("Global").performClick()
      subCategoryList
          .filter { it.year == 2022 }
          .forEach() { onNodeWithText(it.name).assertIsDisplayed() }
      assert(accountingViewModel.uiState.value.globalSelected)
      assert(
          accountingViewModel.uiState.value.subCategoryList ==
              subCategoryList.filter { it.year == 2022 })

      // Tests if a message is shown when no subCategory
      onNodeWithTag("categoryFilterChip").performClick()
      onNodeWithText("Sponsorship").performClick()
      onNodeWithText("No data available with these tags").assertIsDisplayed()
    }
  }

  /** Tests if filter row is scrollable */
  @Test
  fun testsIfFilterRowIsScrollable() {
    with(composeTestRule) {
      onNodeWithTag("filterRow").assertIsDisplayed()
      onNodeWithTag("filterRow").performTouchInput { swipeLeft() }
    }
  }

  /** Tests if total amount is correctly calculated with or without VAT */
  @Test
  fun testTvaFilterAndTotalAmount() {
    with(composeTestRule) {
      onNodeWithTag("yearFilterChip").performClick()
      onNodeWithText("2023").performClick()
      onNodeWithTag("categoryFilterChip").performClick()
      onNodeWithText("Pole").performClick()

      // Test HT
      assert(accountingViewModel.uiState.value.yearFilter == 2023)
      assert(
          accountingViewModel.uiState.value.subCategoryList ==
              subCategoryList.filter { it.year == 2023 && it.categoryUID == "2" })
      val filteredSubCategoryList =
          subCategoryList.filter { it.categoryUID == "2" && it.year == 2023 }.groupBy { it.uid }
      val mockedTotalAmountHT =
          budgetItems
              .filter { it.year == 2023 && it.subcategoryUID in filteredSubCategoryList }
              .map { it.amount }
              .sum()
      val viewModelTotalAmountHT =
          accountingViewModel.uiState.value.amountBudgetHT
              .filter { it.key in filteredSubCategoryList }
              .values
              .sum()
      assert(viewModelTotalAmountHT == mockedTotalAmountHT)

      // Test TTC
      onNodeWithTag("tvaListTag").performClick()
      onNodeWithText("TTC").performClick()
      assert(accountingViewModel.uiState.value.tvaFilterActive)
      val mockedTotalAmountTTC =
          budgetItems
              .filter { it.year == 2023 && it.subcategoryUID in filteredSubCategoryList }
              .map { (it.amount + it.amount * it.tva.rate / 100f).toInt() }
              .sum()
      val viewModelTotalAmountTTC =
          accountingViewModel.uiState.value.amountBudgetTTC
              .filter { it.key in filteredSubCategoryList }
              .values
              .sum()
      assert(viewModelTotalAmountTTC == mockedTotalAmountTTC)
      onNodeWithTag("tvaListTag").performClick()
      onNodeWithText("HT").performClick()
      assert(!accountingViewModel.uiState.value.tvaFilterActive)
    }
  }
}
