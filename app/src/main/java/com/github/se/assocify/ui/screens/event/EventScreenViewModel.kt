package com.github.se.assocify.ui.screens.event

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.event.maptab.EventMapViewModel
import com.github.se.assocify.ui.screens.event.scheduletab.EventScheduleViewModel
import com.github.se.assocify.ui.screens.event.tasktab.EventTaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * A ViewModel of the global Event Screen, to manage the filter chips and the search bar
 *
 * @param eventAPI the Event database to fetch the events
 * @param taskAPI the Task database to fetch the tasks
 */
class EventScreenViewModel(
    navActions: NavigationActions,
    taskAPI: TaskAPI,
    private var eventAPI: EventAPI,
) : ViewModel() {

  val taskListViewModel: EventTaskViewModel = EventTaskViewModel(taskAPI) { showSnackbar(it) }
  val mapViewModel: EventMapViewModel = EventMapViewModel(taskAPI)
  val scheduleViewModel: EventScheduleViewModel = EventScheduleViewModel(navActions, taskAPI)

  private val _uiState: MutableStateFlow<EventScreenState> = MutableStateFlow(EventScreenState())
  val uiState: StateFlow<EventScreenState> = _uiState

  init {
    fetchEvents()
  }

  /** Fetch the events from the database */
  fun fetchEvents() {
    _uiState.value = _uiState.value.copy(loading = true, error = null)
    eventAPI.getEvents(
        { events ->
          updateFilteredEvents(events)
          _uiState.value = _uiState.value.copy(events = events)
          _uiState.value = _uiState.value.copy(loading = false, error = null)
        },
        {
            Log.e("EventScreenViewModel", "Error loading events", it)
            _uiState.value = _uiState.value.copy(loading = false, error = "Error loading events") })
  }

  /** Setup the filtered events depending on the current filters */
  private fun updateFilteredEvents(events: List<Event>) {
    _uiState.value = _uiState.value.copy(selectedEvents = events.filter { isEventSelected(it) })
  }

  /**
   * Modify the current search query
   *
   * @param query the new query
   */
  fun setSearchQuery(query: String) {
    _uiState.value = _uiState.value.copy(searchQuery = query)
  }

  /**
   * Switch to a different tab
   *
   * @param tab the tab we want to switch to
   */
  fun switchTab(tab: EventPageIndex) {
    _uiState.value = _uiState.value.copy(currentTab = tab)
  }

  /**
   * Select an event so that it is counted on the filters
   *
   * @param event the event we want to select
   * @param selected whether we want to select or unselect the event
   */
  fun setEventSelection(event: Event, selected: Boolean) {
    val selectedEvents =
        if (selected) {
          _uiState.value.selectedEvents + event
        } else {
          _uiState.value.selectedEvents - event
        }
    _uiState.value = _uiState.value.copy(selectedEvents = selectedEvents)

    taskListViewModel.setEvents(selectedEvents)
    mapViewModel.setEvents(selectedEvents)
    scheduleViewModel.setEvents(selectedEvents)
  }

  /**
   * Whether the given event is selected or not
   *
   * @param event the event for which we want to know if it selected
   */
  fun isEventSelected(event: Event): Boolean {
    return _uiState.value.selectedEvents.contains(event)
  }

  /** Filter the elements from the current tab depending on the current search query */
  fun searchTaskLists() {
    if (_uiState.value.currentTab == EventPageIndex.Tasks) {
      taskListViewModel.search(_uiState.value.searchQuery)
    } else if (_uiState.value.currentTab == EventPageIndex.Map) {
      mapViewModel
    }
  }

  fun showSnackbar(message: String) {
    CoroutineScope(Dispatchers.Main).launch {
      _uiState.value.snackbarHostState.showSnackbar(
          message = message, duration = SnackbarDuration.Short)
    }
  }
}

/**
 * The state of the event screen
 *
 * @param searchQuery the current search query
 * @param stateBarDisplay whether the search bar is displayed or not
 * @param events the list of events
 * @param selectedEvents the list of selected events
 * @param currentTab the current tab
 * @param error whether an error occurred
 */
data class EventScreenState(
    val loading: Boolean = false,
    val error: String? = null,
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val searchQuery: String = "",
    val stateBarDisplay: Boolean = false,
    val events: List<Event> = emptyList(),
    val selectedEvents: List<Event> = emptyList(),
    val currentTab: EventPageIndex = EventPageIndex.Tasks,
)

/** Event tabs */
enum class EventPageIndex {
  Tasks,
  Map,
  Schedule
}
