package com.github.se.assocify.screens

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingViewModel
import com.github.se.assocify.ui.screens.treasury.accounting.accountingComposables.AddSubcategoryDialog
import com.github.se.assocify.ui.util.SnackbarSystem
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
class AddSubcategoryDialogTest :
    TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)

  val categories =
      listOf(
          AccountingCategory("1", "Pole"),
          AccountingCategory("2", "Events"),
          AccountingCategory("3", "Commission"))

  val subCategories =
      listOf(
          AccountingSubCategory("1", "1", "Pole 1", 0, 2021),
      )

  val accountingCategoryAPI = mockk<AccountingCategoryAPI>()
  val accountingSubCategoryAPI = mockk<AccountingSubCategoryAPI>()
  val balanceAPI = mockk<BalanceAPI>()
  val budgetAPI = mockk<BudgetAPI>()
  lateinit var viewModel: AccountingViewModel

  @Before
  fun setup() {
    CurrentUser.userUid = "userUid"
    CurrentUser.associationUid = "associationUid"
    every { accountingCategoryAPI.getCategories(any(), any(), any()) } answers
        {
          secondArg<(List<AccountingCategory>) -> Unit>().invoke(categories)
        }
    every { accountingSubCategoryAPI.getSubCategories(any(), any(), any()) } answers
        {
          secondArg<(List<AccountingSubCategory>) -> Unit>().invoke(subCategories)
        }
    every { accountingSubCategoryAPI.addSubCategory(any(), any(), any(), any()) } answers
        {
          thirdArg<() -> Unit>().invoke()
        }
    every { balanceAPI.getBalance(any(), any(), any()) } answers
        {
          val onSuccessCallback = secondArg<(List<BalanceItem>) -> Unit>()
          onSuccessCallback(emptyList())
        }
    every { budgetAPI.getBudget(any(), any(), any()) } answers
        {
          val onSuccessCallback = secondArg<(List<BudgetItem>) -> Unit>()
          onSuccessCallback(emptyList())
        }
    viewModel =
        AccountingViewModel(
            accountingCategoryAPI = accountingCategoryAPI,
            accountingSubCategoryAPI = accountingSubCategoryAPI,
            balanceAPI = balanceAPI,
            budgetAPI = budgetAPI,
            SnackbarSystem(SnackbarHostState()))
    composeTestRule.setContent { AddSubcategoryDialog(viewModel) }
    viewModel.showNewSubcategoryDialog()
  }

  @Test
  fun testDisplay() {
    with(composeTestRule) {
      onNodeWithTag("addAccountingCategoryScreen").assertIsDisplayed()
      onNodeWithTag("categoryTitle").assertIsDisplayed()
      onNodeWithTag("cancelButton").assertIsDisplayed()
      onNodeWithTag("yearDropdown").assertIsDisplayed()
      onNodeWithTag("categoryDropdown").assertIsDisplayed()
      onNodeWithTag("categoryNameField").assertIsDisplayed()
      onNodeWithTag("createButton").assertIsDisplayed()
    }
  }

  @Test
  fun testFillingForm() {
    with(composeTestRule) {
      onNodeWithTag("categoryNameField").performClick()
      onNodeWithTag("categoryNameField").performTextInput("le pere noel")
      onNodeWithTag("yearDropdown").performClick()
      onNodeWithText("2021").performClick()
      onNodeWithTag("categoryDropdown").performClick()
      onNodeWithText("Events").performClick()
      onNodeWithTag("createButton").performClick()
    }
  }
}
