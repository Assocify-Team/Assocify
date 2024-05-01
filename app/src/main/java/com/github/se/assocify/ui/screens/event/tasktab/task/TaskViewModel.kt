package com.github.se.assocify.ui.screens.event.tasktab.task

import androidx.compose.material3.SnackbarHostState
import com.github.se.assocify.model.SupabaseClient
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.entities.Task
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.util.DateUtil
import com.github.se.assocify.ui.util.TimeUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class TaskViewModel {

  private var taskApi: TaskAPI
  private val NEW_TASK_TITLE = "New Task"
  private val EDIT_TASK_TITLE = "Edit Task"

  // private val taskApi: TaskAPI
  private val navActions: NavigationActions
  private val taskUid: String

  private val _uiState: MutableStateFlow<TaskState>
  val uiState: StateFlow<TaskState>

  constructor(
      navActions: NavigationActions,
      taskApi: TaskAPI = TaskAPI(SupabaseClient.supabaseClient)
  ) {
    this.navActions = navActions
    this.taskApi = taskApi
    this.taskUid = UUID.randomUUID().toString()
    _uiState = MutableStateFlow(TaskState(isNewTask = true, pageTitle = NEW_TASK_TITLE))
    uiState = _uiState
  }

  constructor(
      taskUid: String,
      navActions: NavigationActions,
      taskApi: TaskAPI = TaskAPI(SupabaseClient.supabaseClient)
  ) {
    this.navActions = navActions
    this.taskApi = taskApi
    this.taskUid = taskUid

    _uiState = MutableStateFlow(TaskState(isNewTask = false, pageTitle = EDIT_TASK_TITLE))
    uiState = _uiState

    taskApi.getTask(taskUid, {
        _uiState.value = _uiState.value.copy(
            title = it.title,
            description = it.description,
            category = it.category,
            staffNumber = it.peopleNeeded.toString(),
            date = DateUtil.toString(it.startTime),
          //  time
            pageTitle = EDIT_TASK_TITLE
        )
    }, { /* handle error */ })
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

  fun saveTask() {
    setTitle(_uiState.value.title)
    setDate(DateUtil.toDate(_uiState.value.date))
    setTime(TimeUtil.toTime(_uiState.value.time))

    if (_uiState.value.titleError != null || _uiState.value.dateError != null) {
      return
    }

    val startTime = DateUtil.toDate(_uiState.value.date).atTime(TimeUtil.toTime(_uiState.value.time)) ?: return

    val task =
        Task(
            uid = taskUid,
            title = _uiState.value.title,
            description = _uiState.value.description,
            category = _uiState.value.category,
            peopleNeeded = _uiState.value.staffNumber.toInt(),)

    taskApi.addTask(task, { navActions.back() }, { /* handle error */ })
  }

  fun deleteTask() {
    if (_uiState.value.isNewTask) {
      navActions.back()
    } else {
      taskApi.deleteTask(taskUid, { navActions.back() }, { /* handle error */ })
      navActions.back()
    }
  }
}

data class TaskState(
    val isNewTask: Boolean,
    val pageTitle: String,
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val staffNumber: String = "",
    val date: String = "",
    val time: String = "",
    val titleError: String? = null,
    val staffNumberError: String? = null,
    val dateError: String? = null,
    val timeError: String? = null,
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
)
