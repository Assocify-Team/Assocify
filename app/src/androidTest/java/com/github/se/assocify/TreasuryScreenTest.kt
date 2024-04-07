package com.github.se.assocify.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.ui.screens.Treasury.TreasuryMainScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.junit4.MockKRule
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TreasuryScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  @Before
  fun testSetup() {
    composeTestRule.setContent { TreasuryMainScreen() }
  }

  @Test
  fun testTabSwitching() {
    onComposeScreen<TreasuryScreen>(composeTestRule) {
      budgetTab.assertIsDisplayed()
      budgetTab.performClick()
      budgetTab.assertIsSelected()

      balanceTab.assertIsDisplayed()
      balanceTab.performClick()
      balanceTab.assertIsSelected()

      myReceiptsTab.assertIsDisplayed()
      myReceiptsTab.performClick()
      myReceiptsTab.assertIsSelected()
    }
  }

  @Test
  fun testTodoListItem() = run {
    onComposeScreen<TreasuryScreen>(composeTestRule) { receiptItemBox.assertIsDisplayed() }
  }

  @Test
  fun createTodo() = run {
    onComposeScreen<TreasuryScreen>(composeTestRule) {
      createReceiptFab.assertIsDisplayed()
      createReceiptFab.performClick()
    }
  }
}
