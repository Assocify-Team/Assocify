package com.github.se.assocify.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.event.EventScreen
import com.github.se.assocify.ui.screens.event.EventScreenViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import java.time.LocalDateTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockEventAPI: EventAPI

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private var tabSelected = false

  @Before
  fun testSetup() {

    every { mockEventAPI.getEvents(any(), any()) } answers
        {
          val onSuccessCallback = arg<(List<Event>) -> Unit>(0)
          onSuccessCallback(emptyList())
        }

    every { mockNavActions.navigateToMainTab(any()) } answers { tabSelected = true }
  }

  @Test
  fun display() {
    composeTestRule.setContent { EventScreen(mockNavActions, EventScreenViewModel(mockEventAPI)) }
    with(composeTestRule) { onNodeWithTag("eventScreen").assertIsDisplayed() }
  }

  @Test
  fun navigate() {
    composeTestRule.setContent { EventScreen(mockNavActions, EventScreenViewModel(mockEventAPI)) }
    with(composeTestRule) {
      onNodeWithTag("mainNavBarItem/treasury").performClick()
      assert(tabSelected)
    }
  }

  @Test
  fun testTabSwitching() {
    composeTestRule.setContent { EventScreen(mockNavActions, EventScreenViewModel(mockEventAPI)) }
    with(composeTestRule) {
      onNodeWithTag("tasksTab").assertIsDisplayed()
      onNodeWithTag("tasksTab").performClick()
      onNodeWithTag("tasksTab").assertIsSelected()

      onNodeWithTag("mapTab").assertIsDisplayed()
      onNodeWithTag("mapTab").performClick()
      onNodeWithTag("mapTab").assertIsSelected()

      onNodeWithTag("scheduleTab").assertIsDisplayed()
      onNodeWithTag("scheduleTab").performClick()
      onNodeWithTag("scheduleTab").assertIsSelected()
    }
  }

  @Test
  fun testFilterChipShowAssoc() {
    every { mockEventAPI.getEvents(any(), any()) } answers
        {
          val events =
              listOf(
                  Event(
                      "1",
                      "filterChipTestEvent1",
                      "a",
                      LocalDateTime.now(),
                      LocalDateTime.now(),
                      "me",
                      "home"))
          val onSuccessCallback = firstArg<(List<Event>) -> Unit>()
          onSuccessCallback(events)
        }
    composeTestRule.setContent { EventScreen(mockNavActions, EventScreenViewModel(mockEventAPI)) }

    with(composeTestRule) {
      val chip = onNodeWithTag("filterChipTestEvent")
      chip.assertIsDisplayed()
      chip.assertIsNotSelected()
      chip.performClick()
      chip.assertIsDisplayed()
      chip.assertIsSelected()
    }
  }

  @Test
  fun searchBarSearchesWell() {
    every { mockEventAPI.getEvents(any(), any()) } answers
        {
          with(composeTestRule) {
            onNodeWithTag("eventAccountIcon").assertIsDisplayed()
            onNodeWithTag("eventSearchIcon").assertIsDisplayed()
            onNodeWithTag("eventSearchButton").performClick()
            onNodeWithTag("eventAccountIcon").assertIsNotDisplayed()
            onNodeWithTag("eventSearchIcon").assertIsNotDisplayed()
            onNodeWithTag("searchBar").assertIsDisplayed()
            onNodeWithTag("searchBar").performTextClearance()
          }
        }
  }
}
