package com.github.se.assocify.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.ui.screens.treasury.accounting.newcategory.AddCategoryPopUp
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.junit4.MockKRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddCategoryScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)

  @Before
  fun display() {
    composeTestRule.setContent { AddCategoryPopUp(show = true) }
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
