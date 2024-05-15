package com.github.se.assocify.screens

import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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
import com.github.se.assocify.model.entities.Task
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.event.tasktab.task.TaskScreen
import com.github.se.assocify.ui.screens.event.tasktab.task.TaskViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalTime
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
      onNodeWithTag("taskScreenTitle").assertIsDisplayed().assertTextContains("New Task")
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

  fun category() {
    with(composeTestRule) {
      onNodeWithTag("categoryField").performClick().performTextInput("Test Category")
      assert(viewModel.uiState.value.category == "Test Category")
      onNodeWithTag("categoryField").assertTextContains("Test Category")
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

  @Test
  fun save() {
    with(composeTestRule) {
      onNodeWithTag("saveButton").performScrollTo().performClick()
      onNodeWithTag("titleField").assertTextContains("Title cannot be empty")
      onNodeWithTag("staffNumberField").assertTextContains("Staff number cannot be empty")
      onNodeWithTag("dateField").assertTextContains("Date cannot be empty")
      onNodeWithTag("timeField").assertTextContains("Time cannot be empty")
      onNodeWithTag("durationField").assertTextContains("Duration cannot be empty")

      onNodeWithTag("titleField").performClick().performTextInput("Test Title")

      onNodeWithTag("staffNumberField").performClick().performTextInput("100")

      onNodeWithTag("dateField").performClick()
      onNodeWithContentDescription("Switch to text input mode").performClick()
      onNodeWithContentDescription("Date", true).performClick().performTextInput("01012021")
      onNodeWithTag("datePickerDialogOk").performClick()
      onNodeWithTag("dateField").assertTextContains("01/01/2021")

      viewModel.setTime(LocalTime.now())
      viewModel.setDuration(LocalTime.of(1, 0))

      assert(viewModel.uiState.value.titleError == null)
      assert(viewModel.uiState.value.staffNumberError == null)
      assert(viewModel.uiState.value.dateError == null)
      assert(viewModel.uiState.value.timeError == null)
      assert(viewModel.uiState.value.durationError == null)

      onNodeWithTag("saveButton").performScrollTo().performClick()
      onNodeWithText("Event is required", true).assertIsDisplayed()

      onNodeWithTag("eventChip").assertTextContains("Select Event")
      onNodeWithTag("eventChip").performScrollTo().performClick()
      onNodeWithText("testEvent1", true).assertIsDisplayed()
      onNodeWithText("testEvent2", true).assertIsDisplayed()
      onNodeWithText("testEvent2", true).performClick()
      onNodeWithTag("eventChip").assertTextContains("testEvent2")

      viewModel.saveTask()
      verify { taskAPI.addTask(any(), any(), any()) }
    }
  }
}

@RunWith(AndroidJUnit4::class)
class EditTaskScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private val eventList =
      listOf<Event>(
          Event(
              "testEvent1",
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

  private val task =
      Task(
          "testTask",
          "testTask",
          "Test Description",
          false,
          OffsetDateTime.now(),
          60,
          5,
          "category",
          "location",
          "testEvent1",
      )

  private var capturedTask: Task? = null

  private val navActions = mockk<NavigationActions>(relaxUnitFun = true)
  private val eventAPI =
      mockk<EventAPI>() {
        every { getEvents(any(), any()) } answers
            {
              firstArg<(List<Event>) -> Unit>().invoke(eventList)
            }
      }
  private val taskAPI =
      mockk<TaskAPI>(relaxUnitFun = true) {
        every { getTask(task.uid, any(), any()) } answers
            {
              secondArg<(Task) -> Unit>().invoke(task)
            }
        every { editTask(any(), any(), any()) } answers { capturedTask = firstArg<Task>() }
      }
  private val viewModel: TaskViewModel = TaskViewModel(task.uid, navActions, taskAPI, eventAPI)

  @Before
  fun testSetup() {
    CurrentUser.userUid = "testUser"
    CurrentUser.associationUid = "testAssociation"
    composeTestRule.setContent { TaskScreen(navActions = navActions, viewModel) }
  }

  @Test
  fun editReceipt() {
    with(composeTestRule) {
      onNodeWithTag("taskScreen").assertIsDisplayed()
      onNodeWithTag("taskScreenTitle").assertIsDisplayed().assertTextContains("Edit Task")
      verify { taskAPI.getTask(task.uid, any(), any()) }
      verify { eventAPI.getEvents(any(), any()) }

      onNodeWithTag("titleField").assertTextContains("testTask")
      onNodeWithTag("descriptionField").assertTextContains("Test Description")
      onNodeWithTag("categoryField").assertTextContains("category")
      onNodeWithTag("staffNumberField").assertTextContains("5")
      Log.e("TEST PRINT LOG", viewModel.uiState.value.event.toString())
      Log.e("TEST PRINT LOG", viewModel.uiState.value.eventList.toString())
      onNodeWithTag("eventChip").assertTextContains("testEvent1")

      onNodeWithTag("titleField").performClick().performTextInput("Test Title 2")

      onNodeWithTag("saveButton").performScrollTo().performClick()
      assert(capturedTask != null)
    }
  }
}
