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
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingFilterBar
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingPage
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingScreen
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import java.lang.Thread.sleep
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountingScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  val categoryList =
      listOf(
          AccountingCategory("1", "Events"),
          AccountingCategory("2", "Pole"),
          AccountingCategory("3", "Commissions"),
          AccountingCategory("4", "Sponsorship"))

  val subCategoryList =
      listOf(
          AccountingSubCategory("4", "2", "Administration", 30, 2023),
          AccountingSubCategory("5", "2", "Presidency", 20, 2023),
          AccountingSubCategory("6", "2", "Communication", 10, 2023),
          AccountingSubCategory("7", "1", "Champachelor", 5000, 2022),
          AccountingSubCategory("8", "1", "Balelec", 5000, 2022),
          AccountingSubCategory("9", "3", "Game*", 3000, 2022),
      )

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

  lateinit var accountingViewModel: AccountingViewModel

  @Before
  fun setup() {
    CurrentUser.userUid = "userId"
    CurrentUser.associationUid = "associationId"
    accountingViewModel =
        AccountingViewModel(mockAccountingCategoryAPI, mockAccountingSubCategoryAPI)
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
}
