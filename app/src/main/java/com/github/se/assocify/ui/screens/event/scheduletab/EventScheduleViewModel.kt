package com.github.se.assocify.ui.screens.event.scheduletab

import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.model.entities.Task
import com.github.se.assocify.ui.util.DateTimeUtil
import com.github.se.assocify.ui.util.DateUtil
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** A ViewModel for the EventScheduleScreen. */
class EventScheduleViewModel(
    private val taskAPI: TaskAPI,
) {
  private val _uiState: MutableStateFlow<ScheduleState> = MutableStateFlow(ScheduleState())
  val uiState: StateFlow<ScheduleState> = _uiState

  /** Initializes the ViewModel by setting the current date and fetching the tasks. */
  init {
    changeDate(LocalDate.now())
    fetchTasks()
  }

  /**
   * Fetches the tasks from the database and updates the UI state. If the tasks could not be loaded,
   * an error message is displayed. If the tasks are loading, a loading indicator is displayed.
   */
  fun fetchTasks() {
    _uiState.value = _uiState.value.copy(loading = true, error = null)
    taskAPI.getTasks(
        { tasks ->
          filterTasks(tasks)
          _uiState.value = _uiState.value.copy(tasks = tasks, loading = false, error = null)
        },
        { _uiState.value = _uiState.value.copy(loading = false, error = "Error loading tasks") })
  }

  /**
   * Filters the tasks based on the current date and the events that are selected.
   *
   * @param tasks The tasks to filter.
   */
  private fun filterTasks(tasks: List<Task> = _uiState.value.tasks) {
    _uiState.value =
        _uiState.value.copy(
            currentDayTasks =
                tasks
                    .filter { it.eventUid in _uiState.value.filteredEventsUid }
                    .filter {
                      DateTimeUtil.toLocalDate(it.startTime) == _uiState.value.currentDate
                    })
  }

  /**
   * Changes the current date and updates the UI state.
   *
   * @param date The new date.
   */
  fun changeDate(date: LocalDate?) {
    if (date == null) return

    _uiState.value = _uiState.value.copy(currentDate = date)
    val dateText =
        when (date) {
          LocalDate.now() -> "Today"
          LocalDate.now().plusDays(1) -> "Tomorrow"
          LocalDate.now().minusDays(1) -> "Yesterday"
          else -> DateUtil.formatVerboseDate(date)
        }
    _uiState.value = _uiState.value.copy(dateText = dateText)
    filterTasks()
  }

  /** Changes the current date to the next day. */
  fun nextDate() {
    changeDate(_uiState.value.currentDate.plusDays(1))
  }

  /** Changes the current date to the previous day. */
  fun previousDate() {
    changeDate(_uiState.value.currentDate.minusDays(1))
  }

  /**
   * Sets the events to filter the tasks by.
   *
   * @param events The events to filter by.
   */
  fun setEvents(events: List<Event>) {
    _uiState.value = _uiState.value.copy(filteredEventsUid = events.map { it.uid })
    filterTasks()
  }
}

/**
 * The state of the EventScheduleViewModel.
 *
 * @param loading Whether the tasks are loading.
 * @param error An error message if the tasks could not be loaded.
 * @param tasks The tasks to display.
 * @param currentDate The current date.
 * @param currentDayTasks The tasks for the current day.
 * @param dateText The text to display for the current date.
 * @param filteredEventsUid The events to filter the tasks by.
 */
data class ScheduleState(
    val loading: Boolean = false,
    val error: String? = null,
    val tasks: List<Task> = emptyList(),
    val currentDate: LocalDate = LocalDate.now(),
    val currentDayTasks: List<Task> = emptyList(),
    val dateText: String = "Today",
    val filteredEventsUid: List<String> = emptyList(),
)
