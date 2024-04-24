package com.github.se.assocify.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.ui.screens.treasury.accounting.Accounting
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountingScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  val options = listOf("Global", "Category", "Commissions", "Events", "Projects", "Other")
  val budgetLines =
      listOf(
          "Logistic Category" to "1000",
          "Communication Category" to "2000",
          "Game*" to "3000",
          "ICBD" to "4000",
          "Balelec" to "5000",
      )
  val categoryMapping =
      mapOf(
          "Global" to
              listOf("Logistic Category", "Communication Category", "Game*", "ICBD", "Balelec"),
          "Category" to listOf("Logistic Category", "Communication Category"),
          "Commissions" to listOf("Game*"),
          "Events" to listOf("ICBD", "Balelec"),
      )

  @Before
  fun setup() {
    CurrentUser.userUid = "userId"
    CurrentUser.associationUid = "associationId"
    composeTestRule.setContent {
      Accounting("Budget", options, budgetLines, categoryMapping)
      // Accounting("Balance", listYear, options, budgetLines, categoryMapping)
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
      budgetLines.forEach { onNodeWithTag("displayLine${it.first}").assertIsDisplayed() }
    }
  }

  /** Tests if the lines are filtered according to the category */
  @Test
  fun testCategoryFiltering() {
    with(composeTestRule) {
      // Initially, select the "Category" filter to change its value to "Events"
      onNodeWithTag("categoryFilterChip").performClick()
      onNodeWithText("Events").performClick()

      // Assert that only the budget lines under "Events" category are shown
      onNodeWithText("ICBD").assertIsDisplayed()
      onNodeWithText("Balelec").assertIsDisplayed()

      // Assert that budget lines not under "Events" are not shown
      onNodeWithText("Logistic Category").assertDoesNotExist()
      onNodeWithText("Communication Category").assertDoesNotExist()
      onNodeWithText("Game*").assertDoesNotExist()

      // Verify the total is recalculated correctly
      val expectedTotal = 9000 // Sum of amounts for "ICBD" and "Balelec"
      onNodeWithText("$expectedTotal").assertIsDisplayed()
    }
  }
}
