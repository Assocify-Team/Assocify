package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Task
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.time.OffsetDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * API for interacting with the task table in the database.
 *
 * @property db the Supabase client
 */
class TaskAPI(private val db: SupabaseClient) : SupabaseApi() {
  private var taskCache: List<Task>? = null

  init {
    updateTaskCache({}, {})
  }

  /**
   * Updates the task cache with the tasks from the database.
   *
   * @param onSuccess called on success with the list of tasks
   * @param onFailure called on failure
   */
  fun updateTaskCache(onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure) {
      val tasks = db.from("task").select().decodeList<SupabaseTask>().map { it.toTask() }
      taskCache = tasks
      onSuccess(tasks)
    }
  }

  /**
   * Gets a task from the database.
   *
   * @param id the id of the task to get
   * @param onSuccess called on success with the task
   * @param onFailure called on failure
   */
  fun getTask(id: String, onSuccess: (Task) -> Unit, onFailure: (Exception) -> Unit) {
    val getFromList = { list: List<Task> ->
      val task = list.find { it.uid == id }
      task?.let { onSuccess(it) } ?: onFailure(Exception("Task with id $id not found"))
    }

    if (taskCache != null) {
      getFromList(taskCache!!)
    } else {
      updateTaskCache(getFromList, onFailure)
    }
  }
  /**
   * Gets all tasks from the database.
   *
   * @param onSuccess called on success with the list of tasks
   * @param onFailure called on failure
   */
  fun getTasks(onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit) {
    if (taskCache != null) {
      onSuccess(taskCache!!)
    } else {
      updateTaskCache(onSuccess, onFailure)
    }
  }

  /**
   * Adds a task to the database.
   *
   * @param task the task to add
   * @param onSuccess called on success
   * @param onFailure called on failure
   */
  fun addTask(task: Task, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure) {
      db.from("task")
          .insert(
              SupabaseTask(
                  uid = task.uid,
                  title = task.title,
                  description = task.description,
                  isCompleted = task.isCompleted,
                  startTime = task.startTime.toString(),
                  duration = task.duration,
                  peopleNeeded = task.peopleNeeded,
                  category = task.category,
                  location = task.location,
                  eventId = task.eventUid))

      taskCache = taskCache?.plus(task)
      onSuccess()
    }
  }

  /**
   * Edits a task in the database.
   *
   * @param title the new title of the task
   * @param description the new description of the task
   * @param isCompleted the new completion status of the task
   * @param startTime the new start time of the task
   * @param duration the new duration of the task
   * @param peopleNeeded the new number of people needed for the task
   * @param category the new category of the task
   * @param location the new location of the task
   * @param onSuccess called on success
   * @param onFailure called on failure
   */
  fun editTask(
      uid: String,
      title: String,
      description: String,
      isCompleted: Boolean,
      startTime: OffsetDateTime,
      duration: Int,
      peopleNeeded: Int,
      category: String,
      location: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      db.from("task").update({
        SupabaseTask::title setTo title
        SupabaseTask::description setTo description
        SupabaseTask::isCompleted setTo isCompleted
        SupabaseTask::startTime setTo startTime.toString()
        SupabaseTask::duration setTo duration
        SupabaseTask::peopleNeeded setTo peopleNeeded
        SupabaseTask::category setTo category
        SupabaseTask::location setTo location
      }) {
        filter { SupabaseTask::uid eq uid }
      }

      taskCache =
          taskCache?.map {
            if (it.uid == uid) {
              it.copy(
                  title = title,
                  description = description,
                  isCompleted = isCompleted,
                  startTime = startTime,
                  peopleNeeded = peopleNeeded,
                  category = category,
                  location = location)
            } else {
              it
            }
          }
      onSuccess()
    }
  }

  /**
   * Edits a task in the database.
   *
   * @param task the task to edit
   * @param onSuccess called on success
   * @param onFailure called on failure
   */
  fun editTask(task: Task, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    editTask(
        task.uid,
        task.title,
        task.description,
        task.isCompleted,
        task.startTime,
        task.duration,
        task.peopleNeeded,
        task.category,
        task.location,
        onSuccess,
        onFailure)
  }

  /**
   * Deletes a task from the database.
   *
   * @param id the id of the task to delete
   * @param onSuccess called on success
   * @param onFailure called on failure
   */
  fun deleteTask(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure) {
      db.from("task").delete { filter { SupabaseTask::uid eq id } }
      onSuccess()
    }
  }

  /**
   * Gets all tasks associated with an event from the database.
   *
   * @param eventId the id of the event to get tasks for
   * @param onSuccess called on success with the list of tasks
   * @param onFailure called on failure
   */
  fun getTasksOfEvent(
      eventId: String,
      onSuccess: (List<Task>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    if (taskCache != null) {
      onSuccess(taskCache!!.filter { it.eventUid == eventId })
    } else {
      updateTaskCache({ onSuccess(it.filter { task -> task.eventUid == eventId }) }, onFailure)
    }
  }

  @Serializable
  private data class SupabaseTask(
      @SerialName("uid") val uid: String,
      @SerialName("title") val title: String,
      @SerialName("description") val description: String,
      @SerialName("is_completed") val isCompleted: Boolean,
      @SerialName("start_time") val startTime: String,
      @SerialName("duration") val duration: Int,
      @SerialName("people_needed") val peopleNeeded: Int,
      @SerialName("category") val category: String,
      @SerialName("location") val location: String,
      @SerialName("event_id") val eventId: String
  ) {
    fun toTask(): Task {
      return Task(
          uid = uid,
          title = title,
          description = description,
          isCompleted = isCompleted,
          startTime = OffsetDateTime.parse(startTime),
          duration = duration,
          peopleNeeded = peopleNeeded,
          category = category,
          location = location,
          eventUid = eventId)
    }
  }
}
