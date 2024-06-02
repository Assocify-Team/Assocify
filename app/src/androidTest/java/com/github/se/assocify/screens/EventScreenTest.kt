package com.github.se.assocify.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.model.entities.Task
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.event.EventScreen
import com.github.se.assocify.ui.screens.event.EventScreenViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import java.time.OffsetDateTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockEventAPI: EventAPI
  @RelaxedMockK lateinit var mockTaskAPI: TaskAPI

  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  private var tabSelected = false

  @Before
  fun testSetup() {
    every { mockEventAPI.getEvents(any(), any()) } answers
        {
          val e1 = Event("eventUID", "testEvent1", "a")
          val onSuccessCallback = arg<(List<Event>) -> Unit>(0)
          onSuccessCallback(listOf(e1))
        }

    every { mockTaskAPI.getTasks(any(), any()) } answers
        {
          val t1 =
              Task(
                  "1",
                  "testTask1",
                  "description",
                  false,
                  OffsetDateTime.now(),
                  60,
                  0,
                  "Committee",
                  "46.518726,6.566615",
                  "eventUID0")
          val t2 =
              Task(
                  "2",
                  "testTask2",
                  "description",
                  false,
                  OffsetDateTime.now(),
                  60,
                  0,
                  "Committee",
                  "46.518726,6.566610",
                  "eventUID")
          val onSuccessCallback = arg<(List<Task>) -> Unit>(0)
          onSuccessCallback(listOf(t1, t2))
        }

    every { mockNavActions.navigateToMainTab(any()) } answers { tabSelected = true }
  }

  @Test
  fun display() {
    composeTestRule.setContent {
      EventScreen(mockNavActions, EventScreenViewModel(mockNavActions, mockTaskAPI, mockEventAPI))
    }
    with(composeTestRule) { onNodeWithTag("eventScreen").assertIsDisplayed() }
  }

  @Test
  fun navigate() {
    composeTestRule.setContent {
      EventScreen(mockNavActions, EventScreenViewModel(mockNavActions, mockTaskAPI, mockEventAPI))
    }
    with(composeTestRule) {
      onNodeWithTag("mainNavBarItem/treasury").performClick()
      assert(tabSelected)
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testTabSwitching() {
    composeTestRule.setContent {
      EventScreen(mockNavActions, EventScreenViewModel(mockNavActions, mockTaskAPI, mockEventAPI))
    }
    with(composeTestRule) {
      onNodeWithTag("scheduleTab").assertIsDisplayed()
      onNodeWithTag("scheduleTab").performClick()
      onNodeWithTag("scheduleTab").assertIsSelected()

      onNodeWithTag("tasksTab").assertIsDisplayed()
      onNodeWithTag("tasksTab").performClick()
      onNodeWithTag("tasksTab").assertIsSelected()

      onNodeWithTag("mapTab").assertIsDisplayed()
      onNodeWithTag("mapTab").performClick()
    }
  }

  @Test
  fun testFilterChipShowAssoc() {
    every { mockEventAPI.getEvents(any(), any()) } answers
        {
          val events = listOf(Event("1", "filterChipTestEvent1", "a"))
          val onSuccessCallback = firstArg<(List<Event>) -> Unit>()
          onSuccessCallback(events)
        }
    composeTestRule.setContent {
      EventScreen(mockNavActions, EventScreenViewModel(mockNavActions, mockTaskAPI, mockEventAPI))
    }

    with(composeTestRule) {
      val chip = onNodeWithTag("filterChipTestEvent")
      chip.assertIsDisplayed()
      chip.assertIsNotSelected()
      chip.performClick()
      chip.assertIsDisplayed()
      chip.assertIsSelected()
    }
  }

  /*
    TODO : these tests completely break the CI after 9mn. No clue why.
           will look for it later.
    @Test
    fun testEPFLMapDisplayed() {
      with(composeTestRule) {
        onNodeWithTag("mapTab").performClick()
        onNodeWithTag("EPFLMapView").assertIsDisplayed()
      }
    }

    @Test
    fun testEPFLMapSwipeMoves() {
      with(composeTestRule) {
        onNodeWithTag("mapTab").performClick()
        onNodeWithTag("EPFLMapView").performClick()
        onNodeWithTag("EPFLMapView").performScrollTo()
      }
    }
  */
  fun searchBarSearchesWell() {
    composeTestRule.setContent {
      EventScreen(mockNavActions, EventScreenViewModel(mockNavActions, mockTaskAPI, mockEventAPI))
    }
    with(composeTestRule) {
      onNodeWithTag("searchBar").assertIsNotDisplayed()
      onNodeWithTag("eventAccountIcon").assertIsDisplayed()
      onNodeWithTag("eventSearchButton").assertIsDisplayed()
      onNodeWithTag("eventSearchButton").performClick()
      onNodeWithTag("eventAccountIcon").assertIsNotDisplayed()
      onNodeWithTag("eventSearchIcon").assertIsNotDisplayed()
      onNodeWithTag("searchBar").assertIsDisplayed()
      onNodeWithTag("searchBarButton").assertHasClickAction()
      onNodeWithTag("searchBarButton").performClick()
      onNodeWithTag("searchBar").assertIsDisplayed()
      onNodeWithTag("dismissBarButton").assertIsDisplayed()
      onNodeWithTag("dismissBarButton").performClick()
      onNodeWithTag("dismissBarButton").assertIsNotDisplayed()
    }
  }

  @Test
  fun testDisplayTask() {
    composeTestRule.setContent {
      EventScreen(mockNavActions, EventScreenViewModel(mockNavActions, mockTaskAPI, mockEventAPI))
    }

    with(composeTestRule) {
      onNodeWithTag("TaskItem").assertIsNotDisplayed()
      onNodeWithTag("filterChipTestEvent").assertIsDisplayed()
      onNodeWithTag("filterChipTestEvent").performClick()
      onNodeWithTag("TaskItem").assertIsDisplayed()
      onNodeWithTag("TaskItem").assertHasClickAction()
      onNodeWithTag("TaskCheckbox").assertHasClickAction()
      onNodeWithTag("TaskCheckbox").performClick()
      onNodeWithTag("TaskCheckbox").assertIsDisplayed()
      onNodeWithTag("TaskItem").assertIsDisplayed()
    }
  }

  @Test
  fun errorEventTest() {
    every { mockEventAPI.getEvents(any(), any()) } answers
        {
          val onFailureError = arg<(Exception) -> Unit>(1)
          onFailureError(IllegalArgumentException("Test error"))
        }
    composeTestRule.setContent {
      EventScreen(mockNavActions, EventScreenViewModel(mockNavActions, mockTaskAPI, mockEventAPI))
    }
    with(composeTestRule) {
      onNodeWithTag("errorMessage").assertIsDisplayed().assertTextContains("Error loading events")
    }
  }

  @Test
  fun errorTaskTest() {
    every { mockTaskAPI.getTasks(any(), any()) } answers
        {
          val onFailureError = arg<(Exception) -> Unit>(1)
          onFailureError(IllegalArgumentException("Test error"))
        }
    composeTestRule.setContent {
      EventScreen(mockNavActions, EventScreenViewModel(mockNavActions, mockTaskAPI, mockEventAPI))
    }
    with(composeTestRule) {
      onNodeWithTag("errorMessage").assertIsDisplayed().assertTextContains("Error loading tasks")
    }
  }

  @Test
  fun errorTaskUpdateTest() {
    every { mockTaskAPI.editTask(any(), any(), any()) } answers
        {
          val onFailureError = arg<(Exception) -> Unit>(2)
          onFailureError(IllegalArgumentException("Test error"))
        }
    composeTestRule.setContent {
      EventScreen(mockNavActions, EventScreenViewModel(mockNavActions, mockTaskAPI, mockEventAPI))
    }
    with(composeTestRule) {
      onNodeWithTag("filterChipTestEvent").performClick()
      onNodeWithTag("TaskCheckbox").performClick()
      onNodeWithTag("snackbar").assertIsDisplayed()
    }
  }
}
