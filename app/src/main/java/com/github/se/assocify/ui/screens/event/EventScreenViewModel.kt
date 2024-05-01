package com.github.se.assocify.ui.screens.event

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.entities.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EventScreenViewModel(private var db: EventAPI) : ViewModel() {
  private val _uiState: MutableStateFlow<EventScreenState> = MutableStateFlow(EventScreenState())
  val uiState: StateFlow<EventScreenState>

  init {
    uiState = _uiState
    updateEventsFromDb()
  }

  private fun updateEventsFromDb() {
    db.getEvents(
        { events ->
          updateFilteredEvents(events)
          _uiState.value = _uiState.value.copy(events = events)
        },
        {
          _uiState.value = _uiState.value.copy(error = true)
          _uiState.value = _uiState.value.copy(errorText = it.message ?: "An error occurred")
        })
  }

  private fun updateFilteredEvents(events: List<Event>) {
    _uiState.value = _uiState.value.copy(selectedEvents = events.filter { isEventSelected(it) })
  }

  fun modifySearchingState(searching: Boolean) {
    _uiState.value = _uiState.value.copy(searching = searching)
  }

  fun modifySearchQuery(query: String) {
    _uiState.value = _uiState.value.copy(searchQuery = query)
  }

  fun switchTab(tab: EventPageIndex) {
    _uiState.value = _uiState.value.copy(currentTab = tab)
  }

  fun setEventSelection(event: Event, selected: Boolean) {
    val selectedEvents =
        if (selected) {
          _uiState.value.selectedEvents + event
        } else {
          _uiState.value.selectedEvents - event
        }
    _uiState.value = _uiState.value.copy(selectedEvents = selectedEvents)
  }

  fun isEventSelected(event: Event): Boolean {
    return _uiState.value.selectedEvents.contains(event)
  }
}

// Index of each tag for navigation
enum class EventPageIndex(val index: Int) {
  TASKS(0),
  MAP(1),
  SCHEDULE(2);

  companion object {
    val NUMBER_OF_PAGES: Int = entries.size
  }
}

data class EventScreenState(
    val searchQuery: String = "",
    val searching: Boolean = false,
    val events: List<Event> = emptyList(),
    val selectedEvents: List<Event> = emptyList(),
    val currentTab: EventPageIndex = EventPageIndex.TASKS,
    val error: Boolean = false,
    val errorText: String = ""
)
