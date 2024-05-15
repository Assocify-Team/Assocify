package com.github.se.assocify.ui.screens.event.tasktab.task

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import com.github.se.assocify.model.SupabaseClient
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.model.entities.Task
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.util.DateUtil
import com.github.se.assocify.ui.util.DurationUtil
import com.github.se.assocify.ui.util.TimeUtil
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel {

  private var taskApi: TaskAPI
  private val eventApi: EventAPI
  private val isNewTask: Boolean
  private val NEW_TASK_TITLE = "New Task"
  private val EDIT_TASK_TITLE = "Edit Task"

  private val navActions: NavigationActions
  private val taskUid: String
  private var eventUid: String? = null

  private val _uiState: MutableStateFlow<TaskState>
  val uiState: StateFlow<TaskState>

  constructor(
      navActions: NavigationActions,
      taskApi: TaskAPI = TaskAPI(SupabaseClient.supabaseClient),
      eventApi: EventAPI = EventAPI(SupabaseClient.supabaseClient)
  ) {
    this.isNewTask = true
    this.navActions = navActions
    this.taskApi = taskApi
    this.eventApi = eventApi
    this.taskUid = UUID.randomUUID().toString()

    _uiState = MutableStateFlow(TaskState(isNewTask = true, pageTitle = NEW_TASK_TITLE))
    uiState = _uiState

    loadEvents()
  }

  constructor(
      taskUid: String,
      navActions: NavigationActions,
      taskApi: TaskAPI = TaskAPI(SupabaseClient.supabaseClient),
      eventApi: EventAPI = EventAPI(SupabaseClient.supabaseClient)
  ) {
    this.isNewTask = false
    this.navActions = navActions
    this.taskApi = taskApi
    this.eventApi = eventApi
    this.taskUid = taskUid

    _uiState = MutableStateFlow(TaskState(isNewTask = false, pageTitle = EDIT_TASK_TITLE))
    uiState = _uiState

    loadTask()
  }

  fun loadTask() {
    _uiState.value = _uiState.value.copy(loading = true, error = null)
    taskApi.getTask(
        taskUid,
        {
          val date = it.startTime.toLocalDate()
          val time = it.startTime.toLocalTime()
          eventUid = it.eventUid

          _uiState.value =
              _uiState.value.copy(
                  isNewTask = false,
                  title = it.title,
                  description = it.description,
                  category = it.category,
                  staffNumber = it.peopleNeeded.toString(),
                  date = DateUtil.toString(date),
                  time = TimeUtil.toString(time),
                  duration = DurationUtil.toString(it.duration))

          loadEvents()
        },
        { _uiState.value = _uiState.value.copy(loading = false, error = "Error loading task") })
  }

  private fun loadEvents() {
    eventApi.getEvents(
        {
          _uiState.value = _uiState.value.copy(eventList = it)
          if (eventUid != null) {
            _uiState.value =
                _uiState.value.copy(
                    event = _uiState.value.eventList.find { event -> eventUid == event.uid })
          }
          _uiState.value = _uiState.value.copy(loading = false, error = null)
        },
        { _uiState.value = _uiState.value.copy(loading = false, error = "Error loading events") })
  }

  fun setEvent(event: Event) {
    _uiState.value = _uiState.value.copy(event = event)
    eventUid = event.uid
  }

  fun setTitle(title: String) {
    _uiState.value = _uiState.value.copy(title = title)
    if (title.isEmpty()) {
      _uiState.value = _uiState.value.copy(titleError = "Title cannot be empty")
    } else {
      _uiState.value = _uiState.value.copy(titleError = null)
    }
  }

  fun setDescription(description: String) {
    _uiState.value = _uiState.value.copy(description = description)
  }

  fun setCategory(category: String) {
    _uiState.value = _uiState.value.copy(category = category)
  }

  fun setStaffNumber(staffNumber: String) {
    if (staffNumber.isEmpty()) {
      _uiState.value = _uiState.value.copy(staffNumber = staffNumber)
      _uiState.value = _uiState.value.copy(staffNumberError = "Staff number cannot be empty")
    } else if (staffNumber.toIntOrNull() != null) {
      _uiState.value = _uiState.value.copy(staffNumber = staffNumber)
      _uiState.value = _uiState.value.copy(staffNumberError = null)
    }
  }

  fun setDate(date: LocalDate?) {
    _uiState.value = _uiState.value.copy(date = DateUtil.toString(date))
    if (date == null) {
      _uiState.value = _uiState.value.copy(dateError = "Date cannot be empty")
    } else {
      _uiState.value = _uiState.value.copy(dateError = null)
    }
  }

  fun setTime(time: LocalTime?) {
    _uiState.value = _uiState.value.copy(time = TimeUtil.toString(time))
    if (time == null) {
      _uiState.value = _uiState.value.copy(timeError = "Time cannot be empty")
    } else {
      _uiState.value = _uiState.value.copy(timeError = null)
    }
  }

  fun setDuration(duration: LocalTime?) {
    _uiState.value = _uiState.value.copy(duration = DurationUtil.toString(duration))
    if (duration == null) {
      _uiState.value = _uiState.value.copy(durationError = "Duration cannot be empty")
    } else {
      _uiState.value = _uiState.value.copy(durationError = null)
    }
  }

  fun saveTask() {
    setTitle(_uiState.value.title)
    setDate(DateUtil.toDate(_uiState.value.date))
    setTime(TimeUtil.toTime(_uiState.value.time))
    setDuration(DurationUtil.toTime(_uiState.value.duration))
    setStaffNumber(_uiState.value.staffNumber)

    if (_uiState.value.titleError != null ||
        _uiState.value.dateError != null ||
        _uiState.value.timeError != null ||
        _uiState.value.durationError != null ||
        _uiState.value.staffNumberError != null) {
      return
    }

    if (_uiState.value.event == null) {
      CoroutineScope(Dispatchers.Main).launch {
        _uiState.value.snackbarHostState.showSnackbar(
            message = "Event is required", duration = SnackbarDuration.Short)
      }
      return
    }

    val event = _uiState.value.event ?: return

    val date = DateUtil.toDate(_uiState.value.date) ?: return
    val time = TimeUtil.toTime(_uiState.value.time) ?: return
    val duration = DurationUtil.toDuration(_uiState.value.duration) ?: return

    val zone = OffsetDateTime.now().offset

    val startTime = OffsetDateTime.of(date, time, zone)

    val task =
        Task(
            uid = taskUid,
            title = _uiState.value.title,
            description = _uiState.value.description,
            isCompleted = false,
            startTime = startTime,
            duration = duration,
            peopleNeeded = _uiState.value.staffNumber.toInt(),
            category = _uiState.value.category,
            location = "", // TODO: Add location
            eventUid = event.uid,
        )

    if (isNewTask) {
      taskApi.addTask(
          task,
          { navActions.back() },
          {
            CoroutineScope(Dispatchers.Main).launch {
              _uiState.value.snackbarHostState.showSnackbar(
                  message = "Failed to create task", duration = SnackbarDuration.Short)
            }
          })
    } else {
      taskApi.editTask(
          task,
          { navActions.back() },
          {
            CoroutineScope(Dispatchers.Main).launch {
              _uiState.value.snackbarHostState.showSnackbar(
                  message = "Failed to save task", duration = SnackbarDuration.Short)
            }
          })
    }
  }

  fun deleteTask() {
    if (_uiState.value.isNewTask) {
      navActions.back()
    } else {
      taskApi.deleteTask(
          taskUid,
          { navActions.back() },
          {
            CoroutineScope(Dispatchers.Main).launch {
              _uiState.value.snackbarHostState.showSnackbar(
                  message = "Failed to delete task", duration = SnackbarDuration.Short)
            }
          })
    }
  }
}

data class TaskState(
    val loading: Boolean = false,
    val error: String? = null,
    val isNewTask: Boolean,
    val pageTitle: String,
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val staffNumber: String = "",
    val date: String = "",
    val time: String = "",
    val duration: String = "",
    val event: Event? = null,
    val eventList: List<Event> = emptyList(),
    val titleError: String? = null,
    val staffNumberError: String? = null,
    val dateError: String? = null,
    val timeError: String? = null,
    val durationError: String? = null,
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
)
