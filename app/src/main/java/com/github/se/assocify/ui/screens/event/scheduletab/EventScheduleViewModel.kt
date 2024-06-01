package com.github.se.assocify.ui.screens.event.scheduletab

import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.model.entities.Task
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.util.DateTimeUtil
import com.github.se.assocify.ui.util.DateUtil
import com.github.se.assocify.ui.util.SnackbarSystem
import com.github.se.assocify.ui.util.SyncSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.LocalTime

/** A ViewModel for the EventScheduleScreen. */
class EventScheduleViewModel(
    private val navActions: NavigationActions,
    private val taskAPI: TaskAPI,
    private val snackbarSystem: SnackbarSystem,
) {
  private val _uiState: MutableStateFlow<ScheduleState> = MutableStateFlow(ScheduleState())
  val uiState: StateFlow<ScheduleState> = _uiState

  private val loadSystem =
      SyncSystem(
          { _uiState.value = _uiState.value.copy(loading = false, refresh = false, error = null) },
          { _uiState.value = _uiState.value.copy(loading = false, refresh = false, error = it) })

  private val refreshSystem =
      SyncSystem(
          { loadSchedule() },
          {
            _uiState.value = _uiState.value.copy(refresh = false)
            snackbarSystem.showSnackbar(it)
          })

  /** Initializes the ViewModel by setting the current date and fetching the tasks. */
  init {
    changeDate(LocalDate.now())
    loadSchedule()
  }

  /**
   * Fetches the tasks from the database and updates the UI state. If the tasks could not be loaded,
   * an error message is displayed. If the tasks are loading, a loading indicator is displayed.
   */
  fun loadSchedule() {
    if (!loadSystem.start(1)) return
    _uiState.value = _uiState.value.copy(loading = true, error = null)
    taskAPI.getTasks(
        { tasks ->
          filterTasks(tasks)
          _uiState.value = _uiState.value.copy(tasks = tasks)
          loadSystem.end()
        },
        { loadSystem.end("Error loading tasks") })
  }

  fun refreshSchedule() {
    if (!refreshSystem.start(1)) return
    _uiState.value = _uiState.value.copy(refresh = true)
    taskAPI.updateTaskCache(
        { refreshSystem.end() }, { refreshSystem.end("Could not refresh tasks") })
  }

  /**
   * Filters the tasks based on the current date and the events that are selected.
   *
   * @param tasks The tasks to filter.
   */
  private fun filterTasks(tasks: List<Task> = _uiState.value.tasks) {
    val filteredTasks = tasks.filter { it.eventUid in _uiState.value.filteredEventsUid }
    val dayTasks = dayTasks(filteredTasks).sortedBy { it.startTime }
    val clampedTasks = clampTasksDuration(dayTasks)
    val overlappingTasks = overlappingTasks(clampedTasks)
    _uiState.value = _uiState.value.copy(currentDayTasks = overlappingTasks)
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

  fun openTask(uid: String) {
    navActions.navigateTo(Destination.EditTask(uid))
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

  /** Filters the tasks to only include tasks that start or end on the current day. */
  private fun dayTasks(tasks: List<Task>): List<Task> {
    // Filter tasks that start on the current day
    val startAtDay =
        tasks.filter {
          // Check if the task starts on the current day
          DateTimeUtil.toLocalDate(it.startTime) == _uiState.value.currentDate
        }

    // Filter tasks that end on the current day
    val endAtDay =
        tasks
            .filter {
              // Check if the task ends on the current day
              DateTimeUtil.toLocalDate(it.startTime.plusMinutes(it.duration.toLong())) ==
                  _uiState.value.currentDate
            }
            .filter {
              // Check if the task is not already in startAtDay
              it !in startAtDay
            }
            .map {
              val endTime = it.startTime.plusMinutes(it.duration.toLong())
              val currentDayDuration = DateTimeUtil.toLocalTime(endTime).toSecondOfDay() / 60
              it.copy(
                  // Set the start time to the beginning of the day
                  startTime =
                      DateTimeUtil.toOffsetDateTime(_uiState.value.currentDate, LocalTime.MIN),
                  // Set the duration to the remaining time of the task
                  duration = currentDayDuration)
            }

    // Filter tasks that are ongoing on the current day
    val ongoingTasks =
        tasks
            .filter {
              // Check if the task starts before the current day and ends after the current day
              DateTimeUtil.toLocalDate(it.startTime) < _uiState.value.currentDate &&
                  DateTimeUtil.toLocalDate(it.startTime.plusMinutes(it.duration.toLong())) >
                      _uiState.value.currentDate
            }
            .map {
              val startTime =
                  DateTimeUtil.toOffsetDateTime(LocalTime.MIN.atDate(_uiState.value.currentDate))
              val duration = (LocalTime.MAX.toSecondOfDay() - LocalTime.MIN.toSecondOfDay()) / 60
              it.copy(startTime = startTime, duration = duration)
            }

    val dayTasks =
        (startAtDay + endAtDay + ongoingTasks).map {
          // Clamp task time to end of day if it spans more than one day
          val endTime = it.startTime.plusMinutes(it.duration.toLong())
          if (DateTimeUtil.toLocalDate(endTime) != _uiState.value.currentDate) {
            val start = DateTimeUtil.toLocalTime(it.startTime).toSecondOfDay() / 60
            val end = LocalTime.MAX.toSecondOfDay() / 60
            val currentDayDuration = end - start
            it.copy(duration = currentDayDuration)
          } else {
            it
          }
        }
    return dayTasks
  }

  /**
   * Clamps the duration of tasks to a minimum of 30 minutes.
   *
   * @param tasks The tasks to clamp.
   */
  private fun clampTasksDuration(tasks: List<Task>): List<Task> {
    return tasks.map {
      if (it.duration < 30) {
        it.copy(duration = 30)
      } else {
        it
      }
    }
  }

  /**
   * Converts a list of tasks to a list of OverlapTasks. OverlapTasks are used to display tasks in
   * the schedule and contain information about the task's position (order) and width (overlaps).
   *
   * @param tasks The tasks to convert.
   */
  private fun overlappingTasks(tasks: List<Task>): List<OverlapTask> {

    val overlapTasks = mutableListOf<OverlapTask>()

    val connectedGroups = findConnectedGroups(tasks)
    for (group in connectedGroups) {

      val collisionsPerTimeSlot = findCollisionsPerTimeSlot(group)
      val maxWidth = collisionsPerTimeSlot.maxOf { it.size }

      val columns = mutableListOf<List<Task>>()
      for (i in 0 until maxWidth) {
        columns.add(emptyList())
      }

      for (task in group) {
        for (i in 0 until maxWidth) {
          val column = columns[i]
          if (column.none { checkOverlap(task, it) }) {
            columns[i] = column + task
            overlapTasks.add(OverlapTask(task, maxWidth, i))
            break
          }
        }
      }
    }

    return overlapTasks
  }

  /**
   * Finds the connected groups of tasks. A connected group is a group of tasks that overlap with
   * each other.
   *
   * @param tasks The tasks to find the connected groups for.
   */
  private fun findConnectedGroups(tasks: List<Task>): List<List<Task>> {
    val connectedGroups = mutableListOf<List<Task>>()
    for (task in tasks) {
      var connected = false
      for (j in connectedGroups.indices) {
        val group = connectedGroups[j]
        if (group.any { checkOverlap(task, it) }) {
          connected = true
          connectedGroups[j] = group + task
          break
        }
      }
      if (!connected) {
        connectedGroups.add(listOf(task))
      }
    }
    return connectedGroups
  }

  /**
   * Finds the number of tasks that overlap in each 30 minute time slot. This is used to determine
   * the maximum number of tasks that overlap in a single time slot.
   *
   * @param tasks The tasks to find the collisions for.
   */
  private fun findCollisionsPerTimeSlot(tasks: List<Task>): List<List<Task>> {
    val firstSlot = DateTimeUtil.toLocalTime(tasks.first().startTime).toSecondOfDay() / 1800
    val collisionsPerTimeSlot = mutableListOf<List<Task>>()
    for (i in 0 until 48) {
      collisionsPerTimeSlot.add(emptyList())
    }
    for (task in tasks) {
      val startSlot = DateTimeUtil.toLocalTime(task.startTime).toSecondOfDay() / 1800 - firstSlot
      val endSlot =
          DateTimeUtil.toLocalTime(task.startTime.plusMinutes(task.duration.toLong()))
              .toSecondOfDay() / 1800 - firstSlot
      for (i in startSlot until endSlot) {
        collisionsPerTimeSlot[i] = collisionsPerTimeSlot[i] + task
      }
    }
    return collisionsPerTimeSlot
  }

  /**
   * Checks if two tasks overlap.
   *
   * @param task1 The first task.
   * @param task2 The second task.
   */
  private fun checkOverlap(task1: Task, task2: Task): Boolean {
    val startOverlap =
        task1.startTime >= task2.startTime &&
            task1.startTime < task2.startTime.plusMinutes(task2.duration.toLong())
    val endOverlap =
        task2.startTime >= task1.startTime &&
            task2.startTime < task1.startTime.plusMinutes(task1.duration.toLong())
    return startOverlap || endOverlap
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
    val refresh: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val currentDate: LocalDate = LocalDate.now(),
    val currentDayTasks: List<OverlapTask> = emptyList(),
    val dateText: String = "Today",
    val filteredEventsUid: List<String> = emptyList(),
)

data class OverlapTask(
    val task: Task,
    val overlaps: Int,
    val order: Int,
)
