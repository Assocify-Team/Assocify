package com.github.se.assocify.screens.profile

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
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

  private var events = listOf(Event("1", "event1", "desc1"), Event("2", "event2", "desc2"))

  private val mockEventAPI =
      mockk<EventAPI> {
        every { getEvents(any(), any()) } answers
            {
              val onSuccess = firstArg<(List<Event>) -> Unit>()
              onSuccess(events)
            }
        every { addEvent(any(), any(), any()) } answers
            {
              events = events + firstArg<Event>()
              secondArg<(String) -> Unit>().invoke(firstArg<Event>().uid)
            }
        every { updateEvent(any(), any(), any()) } answers
            {
              events = events.map { if (it.uid == firstArg<Event>().uid) firstArg<Event>() else it }
              secondArg<() -> Unit>().invoke()
            }
        every { deleteEvent(any(), any(), any()) } answers
            {
              events = events.filter { it.uid != firstArg<String>() }
              secondArg<() -> Unit>().invoke()
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
      for (i in events.indices) onNodeWithTag("deleteEventButton-$i").assertIsDisplayed()
    }
  }

  @Test
  fun addEvent() {
    with(composeTestRule) {
      onNodeWithTag("addEventButton").performClick()
      onNodeWithTag("updateEventDialog").assertIsDisplayed()
      onNodeWithTag("editName").assertIsDisplayed().performTextInput("event3")
      onNodeWithTag("editDescription").assertIsDisplayed().performTextInput("desc3")
      onNodeWithTag("confirmButton").performClick()
      onNodeWithText("event3").assertIsDisplayed()
      onNodeWithText("desc3").assertIsDisplayed()
    }
  }

  @Test
  fun modifyEvent() {
    with(composeTestRule) {
      onAllNodesWithTag("editEventButton").apply {
        fetchSemanticsNodes().forEachIndexed { i, _ ->
          get(i).assertIsDisplayed().assertHasClickAction()
          if (i == events.size - 1) get(i).performClick()
        }
      }
      onNodeWithTag("updateEventDialog").assertIsDisplayed()
      onNodeWithTag("editName").performTextInput("changing ")
      onNodeWithTag("editDescription").assertIsDisplayed().performTextClearance()
      onNodeWithTag("confirmButton").performClick()
      onNodeWithText("changing event2").assertIsDisplayed()
      onNodeWithText("-").assertIsDisplayed()
    }
  }

  @Test
  fun deleteEvent() {
    with(composeTestRule) {
      for (i in events.indices) {
        onNodeWithTag("deleteEventButton-$i").assertIsDisplayed().assertHasClickAction()
        if (i == events.size - 1) {
          val eventName = events[i].name
          onNodeWithTag("deleteEventButton-$i").performClick()
          onNodeWithTag("deleteEventDialog").assertIsDisplayed()
          onNodeWithTag("cancelButton").performClick()
          onNodeWithText(events[i].name).assertIsDisplayed()
          onNodeWithTag("deleteEventButton-$i").performClick()
          onNodeWithText("Are you sure you want to delete the event ${events[i].name}?")
              .assertIsDisplayed()
          onNodeWithTag("confirmButton").performClick()
          onNodeWithText(eventName).assertDoesNotExist()
        }
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
