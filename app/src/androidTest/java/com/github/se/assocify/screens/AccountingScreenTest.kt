package com.github.se.assocify.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingFilterBar
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingPage
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountingScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  val list =
      listOf(
          AccountingSubCategory("1", "Administration Pole", 2000),
          AccountingSubCategory("2", "Presidency Pole", -400),
          AccountingSubCategory("3", "Balelec", 1000),
          AccountingSubCategory("4", "Champachelor", 5000),
          AccountingSubCategory("5", "OGJ", 6000),
          AccountingSubCategory("6", "Communication Fees", 3000))

  @Before
  fun setup() {
    CurrentUser.userUid = "userId"
    CurrentUser.associationUid = "associationId"
    composeTestRule.setContent {
      AccountingFilterBar()
      AccountingScreen(AccountingPage.BUDGET, list, mockNavActions)
    }
  }

  /** Tests if the nodes are displayed */
  @Test
  fun testDisplay() {
    // Test the accounting screen
    with(composeTestRule) {
      onNodeWithTag("AccountingScreen").assertIsDisplayed()
      onNodeWithTag("filterRow").assertIsDisplayed()
      onNodeWithTag("totalLine").assertIsDisplayed()
      onNodeWithTag("yearFilterChip").assertIsDisplayed()
      onNodeWithTag("categoryFilterChip").assertIsDisplayed()
      list.forEach { onNodeWithTag("displayLine${it.name}").assertIsDisplayed() }
    }
  }

  /**
   * Tests if the lines are filtered according to the category
   *
   * @Test fun testCategoryFiltering() { with(composeTestRule) { // Initially, select the "Category"
   *   filter to change its value to "Events" onNodeWithTag("categoryFilterChip").performClick()
   *   onNodeWithText("Events").performClick()
   *
   * // Assert that only the budget lines under "Events" category are shown
   * onNodeWithText("Balelec").assertIsDisplayed()
   * onNodeWithText("Champachelor").assertIsDisplayed()
   *
   * // Assert that budget lines not under "Events" are not shown onNodeWithText("Logistic
   * Category").assertDoesNotExist() onNodeWithText("Communication Category").assertDoesNotExist()
   * onNodeWithText("Game*").assertDoesNotExist()
   *
   * // Verify the total is recalculated correctly val expectedTotal = 6000 // Sum of amounts for
   * "Champachelor" and "Balelec" onNodeWithText("$expectedTotal").assertIsDisplayed() } }
   */

  /** Tests if filter row is scrollable */
  @Test
  fun testsIfFilterRowIsScrollable() {
    with(composeTestRule) {
      onNodeWithTag("filterRow").assertIsDisplayed()
      onNodeWithTag("filterRow").performTouchInput { swipeLeft() }
    }
  }

  /** Tests navigate to budget detailed screen */
  @Test
  fun testNavigateToDetailedScreen() {
    with(composeTestRule) {
      onNodeWithText("Administration Pole").performClick()
      verify { mockNavActions.navigateTo(Destination.BudgetDetailed("Administration Pole")) }
    }
  }
}
