package com.github.se.assocify.ui.screens.profile.events

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.navigation.NavigationActions
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileEventsViewModel(private val eventAPI: EventAPI, navActions: NavigationActions) :
    ViewModel() {
  private val _uiState = MutableStateFlow(ProfileEventsUIState())
  val uiState: StateFlow<ProfileEventsUIState> = _uiState

  init {
    eventAPI.getEvents(
        { eventList -> _uiState.value = _uiState.value.copy(events = eventList) },
        { Log.e("events", "Error loading events") })
  }

  /**
   * Opens the dialogue for modifying an event and sets the event to modify
   *
   * @param event the event to modify
   */
  fun modifyEvent(event: Event) {
    _uiState.value = _uiState.value.copy(modifyingEvent = event)
    _uiState.value = _uiState.value.copy(openDialogue = true)
  }

  /** Closes the dialogue for modifying an event without saving the changes */
  fun clearModifyingEvent() {
    _uiState.value = _uiState.value.copy(modifyingEvent = null)
    _uiState.value = _uiState.value.copy(openDialogue = false)
  }

  /** Deletes the event that is currently being modified */
  fun deleteEvent(event: Event) {
    eventAPI.deleteEvent(
        event.uid,
        {
          eventAPI.getEvents(
              { eventList -> _uiState.value = _uiState.value.copy(events = eventList) },
              { Log.e("events", "Error loading events") })
        },
        { Log.e("events", "Error deleting event") })
  }

  /** Updates the event that is currently being modified */
  fun updateCurrentEvent() {
    eventAPI.updateEvent(
        _uiState.value.modifyingEvent!!,
        {
          eventAPI.getEvents(
              { eventList -> _uiState.value = _uiState.value.copy(events = eventList) },
              { Log.e("events", "Error loading events") })
        },
        { Log.e("events", "Error updating event") })
  }

  /** Opens the dialogue for adding an event */
  fun openAddEvent() {
    _uiState.value = _uiState.value.copy(openDialogue = true)
  }

  /** Adds the event that is being created */
  fun confirmAddEvent() {
    val event =
        Event(
            name = _uiState.value.newName,
            description = _uiState.value.newDescription,
            startDate = _uiState.value.newStartDate,
            endDate = _uiState.value.newEndDate,
            guestsOrArtists = _uiState.value.newGuestsOrArtists,
            location = _uiState.value.newLocation)
    eventAPI.addEvent(
        event,
        {
          eventAPI.getEvents(
              { eventList -> _uiState.value = _uiState.value.copy(events = eventList) },
              { Log.e("events", "Error loading events") })
        },
        { Log.e("events", "Error adding event") })
  }
}

data class ProfileEventsUIState(
    // The list of current events
    val events: List<Event> = emptyList(),
    // The event that is currently being modified
    val modifyingEvent: Event? = null,
    // Whether the dialogue for adding or modifying an event is open
    val openDialogue: Boolean = false,
    // The name for the new event
    val newName: String = "",
    // The description for the new event
    val newDescription: String = "",
    // The start date for the new event
    val newStartDate: OffsetDateTime = OffsetDateTime.now(),
    // The end date for the new event
    val newEndDate: OffsetDateTime = OffsetDateTime.now(),
    // The guests or artists for the new event
    val newGuestsOrArtists: String = "",
    // The location for the new event
    val newLocation: String = ""
)
