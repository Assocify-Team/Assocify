package com.github.se.assocify.ui.screens.profile.events

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.ui.util.DateTimeUtil
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileEventsViewModel(private val eventAPI: EventAPI) : ViewModel() {
  private val _uiState = MutableStateFlow(ProfileEventsUIState())
  val uiState: StateFlow<ProfileEventsUIState> = _uiState

  init {
    eventAPI.getEvents(
        { eventList -> _uiState.value = _uiState.value.copy(events = eventList) },
        { Log.e("events", "Error loading events") })
  }

  /**
   * Opens the dialog for modifying an event and sets the event to modify
   *
   * @param event the event to modify
   */
  fun modifyEvent(event: Event) {
    _uiState.value = _uiState.value.copy(modifyingEvent = event, openDialog = true)
  }

  /** Closes the dialog for modifying an event without saving the changes */
  fun clearModifyingEvent() {
    _uiState.value = _uiState.value.copy(modifyingEvent = null, openDialog = false)
  }

  fun openDeleteDialog(event: Event) {
    _uiState.value = _uiState.value.copy(deletingEvent = event, deleteDialog = true)
  }

  fun clearDeleteDialog() {
    _uiState.value = _uiState.value.copy(deleteDialog = false, deletingEvent = null)
  }

  /** Deletes the event that is currently being modified */
  fun deleteEvent(event: Event) {
    eventAPI.deleteEvent(
        event.uid,
        {
          _uiState.value = _uiState.value.copy(deleteDialog = false, deletingEvent = null)
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
              { eventList ->
                _uiState.value =
                    _uiState.value.copy(
                        events = eventList,
                        openDialog = false,
                        modifyingEvent = null,
                        newName = "",
                        newDescription = "",
                        newStartDate = LocalDate.now(),
                        newEndDate = LocalDate.now(),
                        newGuestsOrArtists = "",
                        newLocation = "")
              },
              { Log.e("events", "Error loading events") })
        },
        { Log.e("events", "Error updating event") })
  }

  /** Opens the dialog for adding an event */
  fun openAddEvent() {
    _uiState.value = _uiState.value.copy(openDialog = true)
  }

  fun updateNewName(name: String) {
    _uiState.value =
        _uiState.value.copy(
            newName = name,
            modifyingEvent =
                _uiState.value.modifyingEvent?.copy(name = name) ?: _uiState.value.modifyingEvent)
  }

  fun updateNewDescription(description: String) {
    _uiState.value =
        _uiState.value.copy(
            newDescription = description,
            modifyingEvent =
                _uiState.value.modifyingEvent?.copy(description = description)
                    ?: _uiState.value.modifyingEvent)
  }

  /** Adds the event that is being created */
  fun confirmAddEvent() {
    val event =
        Event(
            name = _uiState.value.newName,
            description = _uiState.value.newDescription,
            startDate = DateTimeUtil.toOffsetDateTime(_uiState.value.newStartDate, LocalTime.MIN),
            endDate = DateTimeUtil.toOffsetDateTime(_uiState.value.newEndDate, LocalTime.MAX),
            guestsOrArtists = _uiState.value.newGuestsOrArtists,
            location = _uiState.value.newLocation)
    eventAPI.addEvent(
        event,
        {
          eventAPI.getEvents(
              { eventList ->
                _uiState.value =
                    _uiState.value.copy(
                        events = eventList,
                        openDialog = false,
                        newName = "",
                        newDescription = "",
                        newStartDate = LocalDate.now(),
                        newEndDate = LocalDate.now(),
                        newGuestsOrArtists = "",
                        newLocation = "")
              },
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
    // The event that is currently being deleted
    val deletingEvent: Event? = null,
    // Whether the dialog for adding or modifying an event is open
    val openDialog: Boolean = false,
    // Whether the dialog for confirming the deletion of an event is open
    val deleteDialog: Boolean = false,
    // The name for the new event
    val newName: String = "",
    // The description for the new event
    val newDescription: String = "",
    // The start date for the new event
    val newStartDate: LocalDate = LocalDate.now(),
    // The end date for the new event
    val newEndDate: LocalDate = LocalDate.now(),
    // The guests or artists for the new event
    val newGuestsOrArtists: String = "",
    // The location for the new event
    val newLocation: String = ""
)
