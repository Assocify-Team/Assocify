package com.github.se.assocify.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.model.entities.Task
import com.github.se.assocify.ui.screens.event.maptab.EventMapScreen
import com.github.se.assocify.ui.screens.event.maptab.EventMapViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.mockk
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private val tasks: List<Task> =
      listOf(
          Task(
              "1",
              "Task 1",
              "Event 1",
              false,
              OffsetDateTime.now(),
              10,
              "Category",
              "46.518726,6.566613",
              "eventUid"))

  private val events: List<Event> =
      listOf(
          Event(
              "eventUid",
              "Event 1",
              "Event 1",
              OffsetDateTime.now(),
              OffsetDateTime.now(),
              "Guests?",
              "BC"))

  private val taskAPI: TaskAPI =
      mockk<TaskAPI>() {
        every { getTasks(any(), any()) } answers { firstArg<(List<Task>) -> Unit>().invoke(tasks) }
      }

  private val viewModel = EventMapViewModel(taskAPI)

  @Before
  fun testSetup() {
    composeTestRule.setContent { EventMapScreen(viewModel) }
    viewModel.setEvents(events)
  }

  @Test
  fun testSideBar() {
    with(composeTestRule) {
      for (i in 0..23) {
        onNodeWithText(LocalTime.of(i, 0).format(DateTimeFormatter.ofPattern("HH:mm")))
            .assertExists()
      }
    }
  }

  @Test
  fun display() {
    with(composeTestRule) {
      onNodeWithText("Today").assertExists()
      onNodeWithText("Task 1").assertExists()
    }
  }
}
