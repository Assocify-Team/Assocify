package com.github.se.assocify.ui.screens.event

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.ui.screens.event.tasktab.EventTaskViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A ViewModel of the global Event Screen, to manage the filter chips and the search bar
 *
 * @param db the Event database to fetch the events
 * @param taskListViewModel a viewmodel of the screen of the task list
 */
class EventScreenViewModel(
    private var db: EventAPI,
    private val taskListViewModel: EventTaskViewModel
) : ViewModel() {
  private val _uiState: MutableStateFlow<EventScreenState> = MutableStateFlow(EventScreenState())
  val uiState: StateFlow<EventScreenState>

  init {
    uiState = _uiState
    updateEventsFromDb()
  }

  /** Fetch the events from the database */
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

  /** Setup the filtered events depending on the current filters */
  private fun updateFilteredEvents(events: List<Event>) {
    _uiState.value = _uiState.value.copy(selectedEvents = events.filter { isEventSelected(it) })
  }

  /**
   * Display or hide the search bar
   *
   * @param searching display the searchbar or not
   */
  fun modifySearchingState(searching: Boolean) {
    _uiState.value = _uiState.value.copy(stateBarDisplay = searching)
  }

  /**
   * Modify the current search query
   *
   * @param query the new query
   */
  fun modifySearchQuery(query: String) {
    _uiState.value = _uiState.value.copy(searchQuery = query)
  }

  /**
   * Switch to a different tab
   *
   * @param tab the tab we want to switch to
   */
  fun switchTab(tab: EventPageIndex) {
    deactivateSearch()
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
  }

  /**
   * Whether the given event is selected or not
   *
   * @param event the event for which we want to know if it selected
   */
  fun isEventSelected(event: Event): Boolean {
    return _uiState.value.selectedEvents.contains(event)
  }

  /** Stop performing the search and hide the searchbar */
  fun deactivateSearch() {
    when (_uiState.value.currentTab) {
      EventPageIndex.Tasks -> {
        taskListViewModel.search("")
        _uiState.value = _uiState.value.copy(searchQuery = "")
        modifySearchingState(false)
      }
      EventPageIndex.Map -> {
        /*TODO: implement for map screen*/
      }
      EventPageIndex.Schedule -> {
        /*TODO: implement for schedule screen*/
      }
    }
  }

  /** Filter the elements from the current tab depending on the current search query */
  fun searchTaskLists() {
    when (_uiState.value.currentTab) {
      EventPageIndex.Tasks -> {
        taskListViewModel.search(_uiState.value.searchQuery)
      }
      EventPageIndex.Map -> {
        /*TODO: implement for map screen*/
      }
      EventPageIndex.Schedule -> {
        /*TODO: implement for schedule screen*/
      }
    }
  }
}

/**
 * The page index of the current event
 *
 * @param index the index of the event
 */
enum class EventPageIndex {
  Tasks,
  Map,
  Schedule
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
 * @param errorText the error message
 */
data class EventScreenState(
    val searchQuery: String = "",
    val stateBarDisplay: Boolean = false,
    val events: List<Event> = emptyList(),
    val selectedEvents: List<Event> = emptyList(),
    val currentTab: EventPageIndex = EventPageIndex.Tasks,
    val error: Boolean = false,
    val errorText: String = ""
)
