package com.github.se.assocify.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.event.tasktab.task.TaskScreen
import com.github.se.assocify.ui.screens.event.tasktab.task.TaskViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.OffsetDateTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private val eventList =
      listOf<Event>(
          Event(
              "testEvent",
              "testEvent1",
              "Test Event",
              OffsetDateTime.now(),
              OffsetDateTime.now(),
              "5",
              "2022-01-01",
          ),
          Event(
              "testEvent2",
              "testEvent2",
              "Test Event 2",
              OffsetDateTime.now(),
              OffsetDateTime.now(),
              "10",
              "2022-01-01",
          ))

  private val navActions = mockk<NavigationActions>(relaxUnitFun = true)
  private val eventAPI =
      mockk<EventAPI>() {
        every { getEvents(any(), any()) } answers
            {
              firstArg<(List<Event>) -> Unit>().invoke(eventList)
            }
      }
  private val taskAPI = mockk<TaskAPI>(relaxUnitFun = true)
  private val viewModel: TaskViewModel = TaskViewModel(navActions, taskAPI, eventAPI)

  @Before
  fun testSetup() {
    CurrentUser.userUid = "testUser"
    CurrentUser.associationUid = "testAssociation"
    composeTestRule.setContent { TaskScreen(navActions = navActions, viewModel) }
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

  @Test
  fun event() {
    with(composeTestRule) {
      onNodeWithTag("eventChip").assertTextContains("Select Event")
      onNodeWithTag("eventChip").performScrollTo().performClick()
      onNodeWithText("testEvent1", true).assertIsDisplayed()
      onNodeWithText("testEvent2", true).assertIsDisplayed()
      onNodeWithText("testEvent2", true).performClick()
      onNodeWithTag("eventChip").assertTextContains("testEvent2")
      onNodeWithText("testEvent1", true).assertIsNotDisplayed()
    }
  }

  @Test
  fun datePicker() {
    with(composeTestRule) {
      onNodeWithTag("dateField").performClick()
      onNodeWithTag("datePickerDialog").assertIsDisplayed()
      onNodeWithTag("datePickerDialogCancel").performClick()
      onNodeWithTag("datePickerDialog").assertDoesNotExist()
    }
  }

  @Test
  fun timePicker() {
    with(composeTestRule) {
      onNodeWithTag("timeField").performClick()
      onNodeWithTag("timePickerDialog").assertIsDisplayed()
      onNodeWithTag("timePickerDialogCancel").performClick()
      onNodeWithTag("timePickerDialog").assertDoesNotExist()
    }
  }

  @Test
  fun title() {
    with(composeTestRule) {
      onNodeWithTag("titleField").performClick().performTextInput("Test Title")
      assert(viewModel.uiState.value.title == "Test Title")
      onNodeWithTag("titleField").assertTextContains("Test Title")

      onNodeWithTag("titleField").performClick().performTextClearance()
      assert(viewModel.uiState.value.title == "")
      onNodeWithTag("titleField").assertTextContains("Title cannot be empty")
    }
  }

  @Test
  fun description() {
    with(composeTestRule) {
      onNodeWithTag("descriptionField").performClick().performTextInput("Test Description")
      assert(viewModel.uiState.value.description == "Test Description")
      onNodeWithTag("descriptionField").assertTextContains("Test Description")
    }
  }

  @Test
  fun staffAmount() {
    with(composeTestRule) {
      onNodeWithTag("staffNumberField").performScrollTo().performClick().performTextInput("100")
      assert(viewModel.uiState.value.staffNumber == "100")
      onNodeWithTag("staffNumberField").assertTextContains("100")

      onNodeWithTag("staffNumberField").performClick().performTextClearance()
      assert(viewModel.uiState.value.staffNumber == "")
      onNodeWithTag("staffNumberField").assertTextContains("Staff number cannot be empty")

      onNodeWithTag("staffNumberField").performClick().performTextClearance()
      onNodeWithTag("staffNumberField").performTextInput("1")
      onNodeWithTag("staffNumberField").performTextInput(".")
      assert(viewModel.uiState.value.staffNumber == "1")
    }
  }

  @Test
  fun delete() {
    with(composeTestRule) {
      onNodeWithTag("deleteButton").performScrollTo().performClick()
      verify { navActions.back() }
    }
  }

  @Test
  fun back() {
    with(composeTestRule) {
      onNodeWithTag("backButton").performClick()
      verify { navActions.back() }
    }
  }
}
