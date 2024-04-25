package com.github.se.assocify.ui.screens.event.task

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.model.entities.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EventTaskViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(EventTaskState())
    val uiState: StateFlow<EventTaskState>

    init {
        uiState = _uiState
        updateTasks()
    }

  private fun updateTasks() {
        /*TODO: Fetch tasks from the database and update the state*/
    }
  
}



data class EventTaskState(
  val tasks: List<Task> = emptyList(),
  val filteredTasks: List<Task> = emptyList(),
  val filteredEvents : List<Event> = emptyList(),
  val filter: String = "",
  val isFilterActivated: Boolean = false
)