package com.github.se.assocify.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.newcategory.AddAccountingSubCategory
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddCategoryScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var navActions: NavigationActions

  @Before
  fun display() {
    composeTestRule.setContent { AddAccountingSubCategory(navActions = navActions) }
  }

  @Test
  fun testDisplay() {
    with(composeTestRule) {
      onNodeWithTag("addAccountingSubCategoryScreen").assertIsDisplayed()
      onNodeWithTag("subCategoryTitle").assertIsDisplayed()
      onNodeWithTag("cancelButton").assertIsDisplayed()

      onNodeWithTag("categoryDropdown").assertIsDisplayed()
      onNodeWithTag("categoryNameField").assertIsDisplayed()
      onNodeWithTag("valueField").assertIsDisplayed()

      onNodeWithTag("addSubCategoryButton").assertIsDisplayed()
      onNodeWithTag("createButton").assertIsDisplayed()
    }
  }

  @Test
  fun testFillingForm() {
    with(composeTestRule) {
      onNodeWithTag("categoryNameField").performClick()
      onNodeWithTag("categoryNameField").performTextInput("le pere noel")
      onNodeWithTag("valueField").performClick()
      onNodeWithTag("valueField").performTextInput("1000")

      onNodeWithTag("createButton").performClick()
    }
  }

  @Test
  fun testAddCategory() {
    with(composeTestRule) {
      onNodeWithTag("addSubCategoryButton").performClick()

      onNodeWithTag("categoryTitle").assertIsDisplayed()
      onNodeWithTag("newCategoryFieldPopup").assertIsDisplayed()
    }
  }
}
