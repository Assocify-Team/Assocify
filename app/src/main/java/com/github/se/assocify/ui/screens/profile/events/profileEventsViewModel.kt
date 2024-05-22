package com.github.se.assocify.ui.screens.profile.events

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileEventsViewModel(eventAPI: EventAPI, navActions: NavigationActions) : ViewModel() {
  private val _uiState = MutableStateFlow(ProfileEventsUIState())
  val uiState: StateFlow<ProfileEventsUIState> = _uiState

  init {
    eventAPI.getEvents(
        { eventList -> _uiState.value = _uiState.value.copy(events = eventList) },
        { Log.e("events", "Error loading events") })
  }
}

data class ProfileEventsUIState(val events: List<Event> = emptyList())
