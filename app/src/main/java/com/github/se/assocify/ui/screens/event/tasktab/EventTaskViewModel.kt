package com.github.se.assocify.ui.screens.event.tasktab

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.model.entities.Task
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EventTaskViewModel(val db: TaskAPI) : ViewModel() {
  private val _uiState = MutableStateFlow(EventTaskState())
  val uiState: StateFlow<EventTaskState>

  init {
    uiState = _uiState
    updateTasks()
  }

  /** Updates the list of tasks in the UI. */
  private fun updateTasks() {
    db.getTasks(
        { tasks -> _uiState.value = _uiState.value.copy(tasks = tasks) },
        { e ->
          _uiState.value =
              _uiState.value.copy(
                  tasks =
                      listOf(
                          Task(
                              uid = "testUid",
                              title = e.toString(),
                              description = "description",
                              isCompleted = false,
                              startTime = OffsetDateTime.now(),
                              peopleNeeded = 0,
                              category = "Committee",
                              location = "Here",
                              eventUid = "eventUid")))
        })
  }

  /**
   * Updates a task for it be checked or unchecked.
   *
   * @param task the task to update
   * @param checked whether the task is checked or not
   */
  fun checkTask(task: Task, checked: Boolean) {
    _uiState.value =
        _uiState.value.copy(
            tasks =
                _uiState.value.tasks.map {
                  if (it.uid == task.uid) {
                    db.editTask(it.copy(isCompleted = checked), {}, {})
                    it.copy(isCompleted = checked)
                  } else {
                    it
                  }
                })
    filterTasks()
  }

  fun activateSearch(query: String) {
    _uiState.value = _uiState.value.copy(filter = query)
    filterTasks()
  }

  fun setEvents(events: List<Event>) {
    _uiState.value = _uiState.value.copy(filteredEvents = events)
    filterTasks()
  }

  private fun filterTasks() {
    _uiState.value =
        _uiState.value.copy(
            filteredTasks =
                _uiState.value.tasks.filter { task ->
                  _uiState.value.filteredEvents.any { it.uid == task.eventUid } &&
                      (_uiState.value.filter.isEmpty() ||
                          task.title.contains(_uiState.value.filter))
                })
  }
}

/**
 * The state of the event task screen.
 *
 * @param tasks the list of tasks
 * @param filteredTasks the list of tasks after filtering
 * @param filteredEvents the list of events after filtering
 * @param filter the filter string
 */
data class EventTaskState(
    val tasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val filteredEvents: List<Event> = emptyList(),
    val filter: String = ""
)
