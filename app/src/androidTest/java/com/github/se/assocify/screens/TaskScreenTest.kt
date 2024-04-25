package com.github.se.assocify.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.event.tasktab.task.TaskScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private val navActions = mockk<NavigationActions>(relaxUnitFun = true)

  @Before
  fun testSetup() {
    CurrentUser.userUid = "testUser"
    CurrentUser.associationUid = "testAssociation"
    composeTestRule.setContent { TaskScreen(navActions = navActions) }
  }

  @Test
  fun display() {
    with(composeTestRule) {
      onNodeWithTag("taskScreen").assertIsDisplayed()
      onNodeWithTag("taskScreenTitle").assertIsDisplayed()
      onNodeWithTag("backButton").assertIsDisplayed()
      onNodeWithTag("titleField").assertIsDisplayed()
      onNodeWithTag("descriptionField").assertIsDisplayed()
      onNodeWithTag("categoryField").performScrollTo().assertIsDisplayed()
      onNodeWithTag("staffNumberField").performScrollTo().assertIsDisplayed()
      onNodeWithTag("dateField").performScrollTo().assertIsDisplayed()
      onNodeWithTag("timeField").performScrollTo().assertIsDisplayed()
      onNodeWithTag("saveButton").performScrollTo().assertIsDisplayed()
      onNodeWithTag("deleteButton").performScrollTo().assertIsDisplayed()
    }
  }
}
