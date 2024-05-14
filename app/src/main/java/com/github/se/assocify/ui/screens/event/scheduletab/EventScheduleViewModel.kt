package com.github.se.assocify.ui.screens.event.scheduletab

import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.model.entities.Task
import com.github.se.assocify.ui.util.DateUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

class EventScheduleViewModel(
    private val taskAPI: TaskAPI,
) {
  private val _uiState: MutableStateFlow<ScheduleState> = MutableStateFlow(ScheduleState())
  val uiState: StateFlow<ScheduleState> = _uiState

  init {
    changeDate(LocalDate.now())
    fetchTasks()
  }

  fun fetchTasks() {
    _uiState.value = _uiState.value.copy(loading = true, error = null)
    taskAPI.getTasks(
        { tasks ->
          filterTasks(tasks)
          _uiState.value = _uiState.value.copy(tasks = tasks, loading = false, error = null)
        },
        { _uiState.value = _uiState.value.copy(loading = false, error = "Error loading tasks") })
  }

  private fun filterTasks(tasks: List<Task> = _uiState.value.tasks) {
    _uiState.value =
        _uiState.value.copy(
            currentDayTasks =
                tasks
                    .filter { it.eventUid in _uiState.value.filteredEventsUid }
                    .filter { it.startTime.toLocalDate() == _uiState.value.currentDate })
  }

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

  fun nextDate() {
    changeDate(_uiState.value.currentDate.plusDays(1))
  }

  fun previousDate() {
    changeDate(_uiState.value.currentDate.minusDays(1))
  }

  fun setEvents(events: List<Event>) {
    _uiState.value = _uiState.value.copy(filteredEventsUid = events.map { it.uid })
    filterTasks()
  }
}

data class ScheduleState(
    val loading: Boolean = false,
    val error: String? = null,
    val tasks: List<Task> = emptyList(),
    val currentDate: LocalDate = LocalDate.now(),
    val currentDayTasks: List<Task> = emptyList(),
    val dateText: String = "Today",
    val filteredEventsUid: List<String> = emptyList(),
)
