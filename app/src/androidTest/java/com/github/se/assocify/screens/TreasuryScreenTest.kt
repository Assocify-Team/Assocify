package com.github.se.assocify.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.TreasuryScreen
import com.github.se.assocify.ui.screens.treasury.TreasuryViewModel
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingViewModel
import com.github.se.assocify.ui.screens.treasury.receiptstab.ReceiptListViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TreasuryScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)
  private val navActions =
      mockk<NavigationActions>() {
        every { navigateToMainTab(any()) } answers { tabSelected = true }
        every { navigateTo(any()) } answers {}
      }
  private var tabSelected = false
  val categoryList = listOf(AccountingCategory("1", "Events"))
  val subCategoryList =
      listOf(
          AccountingSubCategory("2", "1", "OGJ", 2000, 1000),
          AccountingSubCategory("3", "1", "Subsonic", 100, 50),
      )
  val mockAccountingCategoriesAPI: AccountingCategoryAPI =
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

  val mockBalanceAPI: BalanceAPI =
      mockk<BalanceAPI>() {
        every { getBalance(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(List<BalanceItem>) -> Unit>()
              onSuccessCallback(emptyList())
            }
      }

  val mockBudgetAPI: BudgetAPI =
      mockk<BudgetAPI>() {
        every { getBudget(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(List<BudgetItem>) -> Unit>()
              onSuccessCallback(emptyList())
            }
      }

  val mockReceiptAPI: ReceiptAPI =
      mockk<ReceiptAPI>() {
        every { getAllReceipts(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(List<Receipt>) -> Unit>()
              onSuccessCallback(listOf())
            }
        every { getUserReceipts(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(List<Receipt>) -> Unit>()
              onSuccessCallback(listOf())
            }
      }
  var receiptListViewModel: ReceiptListViewModel = ReceiptListViewModel(navActions, mockReceiptAPI)
  lateinit var accountingViewModel: AccountingViewModel

  @Before
  fun testSetup() {
    CurrentUser.userUid = "testUser"
    CurrentUser.associationUid = "testAssociation"
    accountingViewModel =
        AccountingViewModel(
            mockAccountingCategoriesAPI,
            mockAccountingSubCategoryAPI,
            mockBalanceAPI,
            mockBudgetAPI)
    val viewModel = TreasuryViewModel(navActions, receiptListViewModel, accountingViewModel)
    composeTestRule.setContent {
      TreasuryScreen(navActions, accountingViewModel, receiptListViewModel, viewModel)
    }
  }

  @Test
  fun display() {
    with(composeTestRule) { onNodeWithTag("treasuryScreen").assertIsDisplayed() }
  }

  @Test
  fun navigate() {
    with(composeTestRule) {
      onNodeWithTag("mainNavBarItem/home").performClick()
      assert(tabSelected)
    }
  }

  @Test
  fun testTabSwitching() {
    with(composeTestRule) {
      onNodeWithTag("budgetTab").assertIsDisplayed()
      onNodeWithTag("budgetTab").performClick()
      onNodeWithTag("budgetTab").assertIsSelected()

      onNodeWithTag("balanceTab").assertIsDisplayed()
      onNodeWithTag("balanceTab").performClick()
      onNodeWithTag("balanceTab").assertIsSelected()

      onNodeWithTag("receiptsTab").assertIsDisplayed()
      onNodeWithTag("receiptsTab").performClick()
      onNodeWithTag("receiptsTab").assertIsSelected()
    }
  }

  @Test
  fun createReceipt() = run {
    with(composeTestRule) {
      onNodeWithTag("createReceipt").assertIsDisplayed()
      onNodeWithTag("createReceipt").performClick()
    }
  }

  @Test
  fun topBar() = run {
    with(composeTestRule) {
      onNodeWithTag("accountIconButton").assertIsDisplayed()
      onNodeWithTag("accountIconButton").performClick()
    }
  }

  @Test
  fun searchBar() = run {
    with(composeTestRule) {
      // First open the search bar
      onNodeWithTag("searchBar").assertIsNotDisplayed()
      onNodeWithTag("searchIconButton").performClick()
      onNodeWithTag("searchBar").assertIsDisplayed()
      onNodeWithTag("searchBar").onChild().assertIsDisplayed()
      onNodeWithTag("searchBar").onChild().performTextInput("salut cest bob lennon")
      onNodeWithTag("searchBar").onChild().assertTextContains("salut cest bob lennon")
      onNodeWithTag("searchClearButton").performClick()
      onNodeWithTag("searchBar").onChild().assertTextContains("")
      onNodeWithTag("searchBackButton").performClick()
      onNodeWithTag("searchBar").assertIsNotDisplayed()
    }
  }

  @Test
  fun searchBarInBudgetAndBalance() {
    with(composeTestRule) {
      onNodeWithTag("budgetTab").assertIsDisplayed()
      onNodeWithTag("budgetTab").performClick()

      onNodeWithTag("searchIconButton").performClick()
      onNodeWithTag("searchBar").assertIsDisplayed()
      onNodeWithTag("searchBar").onChild().assertIsDisplayed()
      onNodeWithTag("searchBar").onChild().performTextInput("Presidency")
      onNodeWithTag("searchBar").onChild().assertTextContains("Presidency")
      onNodeWithTag("searchClearButton").performClick()
      onNodeWithTag("searchBar").onChild().assertTextContains("")
      onNodeWithTag("searchBackButton").performClick()
      onNodeWithTag("searchBar").assertIsNotDisplayed()

      onNodeWithTag("balanceTab").assertIsDisplayed()
      onNodeWithTag("balanceTab").performClick()

      onNodeWithTag("searchIconButton").performClick()
      onNodeWithTag("searchBar").onChild().performTextInput("Presidency")
      onNodeWithTag("searchBar").onChild().assertTextContains("Presidency")
    }
  }

  @Test
  fun receiptLoading() {
    every { mockReceiptAPI.getAllReceipts(any(), any()) } answers
        {
          secondArg<(Exception) -> Unit>().invoke(Exception("error"))
        }
    every { mockReceiptAPI.getUserReceipts(any(), any()) } answers
        {
          secondArg<(Exception) -> Unit>().invoke(Exception("error"))
        }
    with(composeTestRule) {
      receiptListViewModel.updateReceipts()
      onNodeWithTag("errorMessage").assertIsDisplayed().assertTextContains("Error loading receipts")
    }
  }
}
