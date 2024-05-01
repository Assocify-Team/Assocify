package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Task
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.time.OffsetDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * API for interacting with the task table in the database.
 *
 * @property db the Supabase client
 */
class TaskAPI(private val db: SupabaseClient) : SupabaseApi() {
  private val scope = CoroutineScope(Dispatchers.Main)

  /**
   * Gets a task from the database.
   *
   * @param id the id of the task to get
   * @param onSuccess called on success with the task
   * @param onFailure called on failure
   */
  fun getTask(id: String, onSuccess: (Task) -> Unit, onFailure: (Exception) -> Unit) {
    scope.launch {
      try {
        val task =
            db.from("task")
                .select {
                  filter { SupabaseTask::uid eq id }
                  limit(1)
                  single()
                }
                .decodeAs<SupabaseTask>()
        onSuccess(task.toTask())
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }
  /**
   * Gets all tasks from the database.
   *
   * @param onSuccess called on success with the list of tasks
   * @param onFailure called on failure
   */
  fun getTasks(onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit) {
    scope.launch {
      try {
        val tasks = db.from("task").select().decodeList<SupabaseTask>()
        onSuccess(tasks.map { it.toTask() })
      } catch (e: Exception) {
        onFailure(e)
      }
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
    scope.launch {
      try {
        db.from("task")
            .insert(
                SupabaseTask(
                    uid = task.uid,
                    title = task.title,
                    description = task.description,
                    isCompleted = task.isCompleted,
                    startTime = task.startTime.toString(),
                    peopleNeeded = task.peopleNeeded,
                    category = task.category,
                    location = task.location,
                    eventId = task.eventUid))

        onSuccess()
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

  /**
   * Edits a task in the database.
   *
   * @param title the new title of the task
   * @param description the new description of the task
   * @param isCompleted the new completion status of the task
   * @param startTime the new start time of the task
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
      peopleNeeded: Int,
      category: String,
      location: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    scope.launch {
      try {
        db.from("task").update({
          SupabaseTask::title setTo title
          SupabaseTask::description setTo description
          SupabaseTask::isCompleted setTo isCompleted
          SupabaseTask::startTime setTo startTime.toString()
          SupabaseTask::peopleNeeded setTo peopleNeeded
          SupabaseTask::category setTo category
          SupabaseTask::location setTo location
        }) {
          filter { SupabaseTask::uid eq uid }
        }
        onSuccess()
      } catch (e: Exception) {
        onFailure(e)
      }
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
    scope.launch {
      try {
        db.from("task").delete { filter { SupabaseTask::uid eq id } }
        onSuccess()
      } catch (e: Exception) {
        onFailure(e)
      }
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
    scope.launch {
      try {
        val tasks =
            db.from("task")
                .select { filter { SupabaseTask::eventId eq eventId } }
                .decodeList<SupabaseTask>()
        onSuccess(tasks.map { it.toTask() })
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

  @Serializable
  private data class SupabaseTask(
      @SerialName("id") val uid: String,
      @SerialName("title") val title: String,
      @SerialName("description") val description: String,
      @SerialName("is_completed") val isCompleted: Boolean,
      @SerialName("start_time") val startTime: String,
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
          peopleNeeded = peopleNeeded,
          category = category,
          location = location,
          eventUid = eventId)
    }
  }
}
