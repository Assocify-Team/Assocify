package com.github.se.assocify.ui.screens.profile.events

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileEventsViewModel(private val eventAPI: EventAPI, navActions: NavigationActions) : ViewModel() {
  private val _uiState = MutableStateFlow(ProfileEventsUIState())
  val uiState: StateFlow<ProfileEventsUIState> = _uiState

  init {
    eventAPI.getEvents(
        { eventList -> _uiState.value = _uiState.value.copy(events = eventList) },
        { Log.e("events", "Error loading events") })
  }

    fun modifyEvent(event: Event) {
        _uiState.value = _uiState.value.copy(modifyingEvent = event)
    }

    fun clearModifyingEvent() {
        _uiState.value = _uiState.value.copy(modifyingEvent = null)
    }

    fun deleteCurrentEvent() {
        eventAPI.deleteEvent(_uiState.value.modifyingEvent!!.uid,
            { eventAPI.getEvents(
                { eventList -> _uiState.value = _uiState.value.copy(events = eventList) },
                { Log.e("events", "Error loading events") }) },
            { Log.e("events", "Error deleting event") })
    }

    fun updateCurrentEvent() {
        eventAPI.updateEvent(_uiState.value.modifyingEvent!!,
            { eventAPI.getEvents(
                { eventList -> _uiState.value = _uiState.value.copy(events = eventList) },
                { Log.e("events", "Error loading events") }) },
            { Log.e("events", "Error updating event") })
    }

    fun addEvent(event: Event) {
        eventAPI.addEvent(event,
            { eventAPI.getEvents(
                { eventList -> _uiState.value = _uiState.value.copy(events = eventList) },
                { Log.e("events", "Error loading events") }) },
            { Log.e("events", "Error adding event") })
    }
}

data class ProfileEventsUIState(
    val events: List<Event> = emptyList(),
    val modifyingEvent: Event? = null,

)
