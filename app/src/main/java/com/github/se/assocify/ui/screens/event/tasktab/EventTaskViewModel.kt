package com.github.se.assocify.ui.screens.event.tasktab

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.model.entities.Task
import com.github.se.assocify.ui.util.SyncSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EventTaskViewModel(val db: TaskAPI, val showSnackbar: (String) -> Unit) : ViewModel() {
  private val _uiState = MutableStateFlow(EventTaskState())
  val uiState: StateFlow<EventTaskState>

  private val loadSystem =
      SyncSystem(
          { _uiState.value = _uiState.value.copy(loading = false, refresh = false, error = null) },
          { _uiState.value = _uiState.value.copy(loading = false, refresh = false, error = it) })

  private val refreshSystem = SyncSystem({ updateTasks() }, {
      _uiState.value = _uiState.value.copy(refresh = false)
      showSnackbar(it)
  })

  init {
    uiState = _uiState
    updateTasks()
  }

  /** Updates the list of tasks in the UI. */
  fun updateTasks() {
    if (!loadSystem.start(1)) return

    _uiState.value = _uiState.value.copy(loading = true, error = null)

    db.getTasks(
        { tasks ->
          _uiState.value = _uiState.value.copy(tasks = tasks)
          filterTasks()
          loadSystem.end()
        },
        { loadSystem.end("Error loading tasks") })
  }

  fun refreshTasks() {
    if (!refreshSystem.start(1)) return
    _uiState.value = _uiState.value.copy(refresh = true)
    db.updateTaskCache({ refreshSystem.end() }, { refreshSystem.end("Could not refresh tasks") })
  }

  /**
   * Updates a task for it be checked or unchecked.
   *
   * @param task the task to update
   * @param checked whether the task is checked or not
   */
  fun checkTask(task: Task, checked: Boolean) {
    db.editTask(
        task.copy(isCompleted = checked),
        {
          _uiState.value =
              _uiState.value.copy(
                  tasks =
                      _uiState.value.tasks.map {
                        if (it.uid != task.uid) it else it.copy(isCompleted = checked)
                      })
          filterTasks()
        },
        { showSnackbar("Couldn't update task state") })
  }

  /**
   * Searches for tasks with a given query.
   *
   * @param query the query to search for
   */
  fun search(query: String) {
    _uiState.value = _uiState.value.copy(filter = query)
    filterTasks()
  }

  /**
   * Sets the events for the tasks.
   *
   * @param events the list of events
   */
  fun setEvents(events: List<Event>) {
    _uiState.value = _uiState.value.copy(filteredEvents = events)
    filterTasks()
  }

  /** Filters the tasks based on the search query and events. */
  private fun filterTasks() {
    _uiState.value =
        _uiState.value.copy(
            filteredTasks =
                _uiState.value.tasks.filter { task ->
                  (_uiState.value.filter.isEmpty() ||
                      task.title.lowercase().contains(_uiState.value.filter.lowercase()))
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
    val loading: Boolean = false,
    val error: String? = null,
    val refresh: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val filteredEvents: List<Event> = emptyList(),
    val filter: String = ""
)
