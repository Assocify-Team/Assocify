package com.github.se.assocify.ui.screens.event

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.entities.Event
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EventScreenViewModel : ViewModel() {
  private val _uiState: MutableStateFlow<EventScreenState> = MutableStateFlow(EventScreenState())
  val uiState: StateFlow<EventScreenState>

  init {
    uiState = _uiState
  }

  fun modifySearchingState(searching: Boolean) {
    _uiState.value = _uiState.value.copy(searching = searching)
  }

  fun modifySearchQuery(query: String) {
    _uiState.value = _uiState.value.copy(searchQuery = query)
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
    val tasks: List<Tasks> = emptyList(),
    val currentTab: EventPageIndex = EventPageIndex.TASKS
)
