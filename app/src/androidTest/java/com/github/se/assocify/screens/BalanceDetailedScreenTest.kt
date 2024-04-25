package com.github.se.assocify.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BalanceItem
import com.github.se.assocify.model.entities.MaybeRemotePhoto
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.model.entities.Status
import com.github.se.assocify.model.entities.TVA
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.balance.BalanceDetailedScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import java.time.LocalDate
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BalanceDetailedScreenTest :
    TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  val subCategory =
      AccountingSubCategory("subCategoryUid", "Logistics Pole", AccountingCategory("Pole"), 1205)
  val receipt =
      Receipt(
          "1",
          "receipt1",
          "url",
          LocalDate.now(),
          100,
          Status.Unapproved,
          MaybeRemotePhoto.Remote("path"))
  val balanceItems =
      listOf(
          BalanceItem(
              "1",
              "pair of scissors",
              5,
              TVA.TVA_8,
              "scissors for paper cutting",
              subCategory,
              LocalDate.of(2024, 4, 14),
              receipt,
              "François Théron",
              Status.Unapproved),
          BalanceItem(
              "2",
              "sweaters",
              1000,
              TVA.TVA_8,
              "order for 1000 sweaters",
              subCategory,
              LocalDate.of(2024, 3, 11),
              receipt,
              "Rayan Boucheny",
              Status.Archived),
          BalanceItem(
              "3",
              "chairs",
              200,
              TVA.TVA_8,
              "order for 200 chairs",
              subCategory,
              LocalDate.of(2024, 1, 14),
              receipt,
              "Sidonie Bouthors",
              Status.PaidBack))

  @Before
  fun setup() {
    CurrentUser.userUid = "userId"
    CurrentUser.associationUid = "associationId"
    composeTestRule.setContent { BalanceDetailedScreen("subcategoryuid", mockNavActions) }
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
      onNodeWithTag("statusListTag").assertIsDisplayed()
      onNodeWithTag("tvaListTag").assertIsDisplayed()
      balanceItems.forEach { onNodeWithTag("displayItem${it.uid}").assertIsDisplayed() }
    }
  }

  /** Tests if the total amount correspond to the sum of the items */
  @Test
  fun testTotalAmount() {
    // Test the accounting screen
    with(composeTestRule) {
      onNodeWithTag("totalItems").assertIsDisplayed()
      var total = 0
      balanceItems.forEach { total += it.amount }
      onNodeWithText(total.toString())
    }
  }

  /** Tests if the lines are filtered according to the status */
  @Test
  fun testStatusFiltering() {
    with(composeTestRule) {
      // Initially, select the "Status" filter to change its value to "Unapproved"
      onNodeWithTag("statusListTag").performClick()
      onNodeWithText("Unapproved").performClick()

      // Assert that only the budget lines under "Unapproved" status are shown
      onNodeWithText("pair of scissors").assertIsDisplayed()

      // Assert that budget lines not under "Unapproved" are not shown
      onNodeWithText("sweaters").assertDoesNotExist()
    }
  }
}
