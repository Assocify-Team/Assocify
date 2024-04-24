package com.github.se.assocify.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
import io.mockk.mockk
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

  private val navActions = mockk<NavigationActions>()
  private var tabSelected = false

  @Before
  fun testSetup() {

    every { mockEventAPI.getEvents(any(), any()) } answers
        {
          val onSuccessCallback = arg<(List<Event>) -> Unit>(0)
          onSuccessCallback(emptyList())
        }

    every { navActions.navigateToMainTab(any()) } answers { tabSelected = true }
    composeTestRule.setContent { EventScreen(navActions, EventScreenViewModel(mockEventAPI)) }
  }

  @Test
  fun display() {
    with(composeTestRule) { onNodeWithTag("eventScreen").assertIsDisplayed() }
  }

  @Test
  fun navigate() {
    with(composeTestRule) {
      onNodeWithTag("mainNavBarItem/treasury").performClick()
      assert(tabSelected)
    }
  }

  @Test
  fun testTabSwitching() {
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
  fun testMultipleAssoc() {
    every { mockEventAPI.getEvents(any(), any()) } answers
        {
          val events =
              listOf(
                  Event(
                      "1",
                      "testEvent1",
                      "a",
                      LocalDateTime.now(),
                      LocalDateTime.now(),
                      "me",
                      "home"),
                  Event(
                      "2",
                      "testEvent2",
                      "a",
                      LocalDateTime.now(),
                      LocalDateTime.now(),
                      "me",
                      "home"),
                  Event(
                      "3",
                      "testEvent3",
                      "a",
                      LocalDateTime.now(),
                      LocalDateTime.now(),
                      "me",
                      "home"))
          val onSuccessCallback = arg<(List<Event>) -> Unit>(0)
          onSuccessCallback(events)
          with(composeTestRule) {
            onNodeWithTag("testEvent1").assertIsDisplayed()

            onNodeWithTag("testEvent2").assertIsDisplayed()

            onNodeWithTag("testEvent3").assertIsDisplayed()
          }
        }
  }
}
