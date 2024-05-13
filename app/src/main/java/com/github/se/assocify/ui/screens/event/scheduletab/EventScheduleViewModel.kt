package com.github.se.assocify.ui.screens.event.scheduletab

import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.entities.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EventScheduleViewModel(
    private val taskAPI: TaskAPI,
) {
  private val _uiState: MutableStateFlow<ScheduleState> = MutableStateFlow(ScheduleState())
  val uiState: StateFlow<ScheduleState> = _uiState

  init {
    fetchTasks()
  }

  private fun fetchTasks() {
    taskAPI.getTasks({ tasks -> _uiState.value.copy(tasks = tasks) }, { /* Handle error */})
  }
}

data class ScheduleState(
    val loading: Boolean = false,
    val error: String? = null,
    val tasks: List<Task> = emptyList(),
)
