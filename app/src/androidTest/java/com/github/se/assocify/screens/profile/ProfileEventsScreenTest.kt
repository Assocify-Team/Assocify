package com.github.se.assocify.screens.profile

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.profile.events.ProfileEventsScreen
import com.github.se.assocify.ui.screens.profile.events.ProfileEventsViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileEventsScreenTest :
    TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private val navActions = mockk<NavigationActions>()
  private var goBack = false

  private val events = listOf(Event("1", "event1", "desc1"), Event("2", "event2", "desc2"))

  private val mockEventAPI =
      mockk<EventAPI> {
        every { getEvents(any(), any()) } answers
            {
              val onSuccess = firstArg<(List<Event>) -> Unit>()
              onSuccess(events)
            }
      }

  @Before
  fun testSetup() {
    CurrentUser.userUid = "1"
    CurrentUser.associationUid = "asso"

    every { navActions.back() } answers { goBack = true }

    composeTestRule.setContent {
      ProfileEventsScreen(navActions = navActions, ProfileEventsViewModel(mockEventAPI))
    }
  }

  @Test
  fun display() {
    with(composeTestRule) {
      onNodeWithTag("ProfileEvents Screen").assertIsDisplayed()
      onNodeWithTag("addEventButton").assertIsDisplayed().assertHasClickAction()
      events.forEach { onNodeWithText(it.name).assertIsDisplayed() }
      onAllNodesWithTag("editEventButton").assertCountEquals(events.size)
      onAllNodesWithTag("deleteEventButton").assertCountEquals(events.size)
    }
  }

  @Test
  fun addEvent() {
    with(composeTestRule) {
      onNodeWithTag("addEventButton").performClick()
      onNodeWithTag("updateEventDialog").assertIsDisplayed()
    }
  }

  @Test
  fun modifyEvent() {
    with(composeTestRule) {
      onAllNodesWithTag("editEventButton").apply {
        fetchSemanticsNodes().forEachIndexed { i, _ ->
          get(i).assertIsDisplayed().assertHasClickAction()
            if (i == events.size-1) get(i).performClick()
        }
      }

      onNodeWithTag("updateEventDialog").assertIsDisplayed()
    }
  }

    @Test
  fun deleteEvent() {
    with(composeTestRule) {
      onAllNodesWithTag("deleteEventButton").apply {
        fetchSemanticsNodes().forEachIndexed { i, _ ->
          get(i).assertIsDisplayed().assertHasClickAction()
            if (i == events.size - 1) get(i).performClick()
        }
        onNodeWithTag("deleteEventDialog").assertIsDisplayed()
      }
    }
  }

  @Test
  fun goBack() {
    with(composeTestRule) {
      onNodeWithTag("backButton").performClick()
      assert(goBack)
    }
  }
}
