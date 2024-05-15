package com.github.se.assocify.ui.screens.event.maptab

import android.util.Log
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.model.entities.MapMarkerData
import com.github.se.assocify.model.entities.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.osmdroid.util.GeoPoint

class EventMapViewModel(private val taskAPI: TaskAPI) {
  private val _uiState: MutableStateFlow<EventMapState> = MutableStateFlow(EventMapState())
  val uiState: StateFlow<EventMapState> = _uiState

  init {
    // First, get all tasks
    fetchTasks()
    // Then, fetch the markers
    fetchMarkers()
  }

  fun fetchTasks() {
    _uiState.value = _uiState.value.copy(loading = true, error = null)
    taskAPI.getTasks(
        { tasks ->
          filterTasks(tasks)
          _uiState.value = _uiState.value.copy(tasks = tasks, loading = false, error = null)
        },
        { _uiState.value = _uiState.value.copy(loading = false, error = "Error loading tasks") })
    Log.w("mdrrrrr: ", _uiState.value.tasks.size.toString())
  }

  private fun filterTasks(tasks: List<Task> = _uiState.value.tasks) {
    // Filters requires cleaning the markers
    _uiState.value = _uiState.value.copy(markers = emptyList())
    // Now filter
    _uiState.value =
        _uiState.value.copy(
            currentEventTasks = tasks.filter { it.eventUid in _uiState.value.filteredEventsUid })
  }

  fun setEvents(events: List<Event>) {
    _uiState.value = _uiState.value.copy(filteredEventsUid = events.map { it.uid })
    filterTasks()
    fetchMarkers()
  }

  private fun fetchMarkers() {
    val tasks = _uiState.value.currentEventTasks
    for (task in tasks) {
      var location = GeoPoint(46.518726, 6.566613)
      if (task.location.isNotEmpty()) location = GeoPoint.fromDoubleString(task.location, ',')
      val marker =
          MapMarkerData(name = task.title, position = location, description = task.description)
      _uiState.value = _uiState.value.copy(markers = _uiState.value.markers + marker)
    }
  }
}

data class EventMapState(
    val loading: Boolean = false,
    val error: String? = null,
    val tasks: List<Task> = emptyList(),
    val currentEventTasks: List<Task> = emptyList(),
    val filteredEventsUid: List<String> = emptyList(),
    val markers: List<MapMarkerData> = emptyList()
)
