package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Task
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class TaskAPI(private val db: SupabaseClient) : SupabaseApi() {
  private val scope = CoroutineScope(Dispatchers.Main)

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
                    location = task.location))

        onSuccess()
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

  fun editTask(
      title: String,
      description: String,
      isCompleted: Boolean,
      startTime: LocalDate,
      peopleNeeded: Int,
      category: String,
      location: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    scope.launch {
      try {
        db.from("task")
            .update({
              SupabaseTask::title setTo title
              SupabaseTask::description setTo description
              SupabaseTask::isCompleted setTo isCompleted
              SupabaseTask::startTime setTo startTime.toString()
              SupabaseTask::peopleNeeded setTo peopleNeeded
              SupabaseTask::category setTo category
              SupabaseTask::location setTo location
            })
        onSuccess()
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

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

  @Serializable
  private data class SupabaseTask(
      @SerialName("uid") val uid: String,
      @SerialName("title") val title: String,
      @SerialName("description") val description: String,
      @SerialName("is_completed") val isCompleted: Boolean,
      @SerialName("start_time") val startTime: String,
      @SerialName("people_needed") val peopleNeeded: Int,
      @SerialName("category") val category: String,
      @SerialName("location") val location: String
  ) {
    fun toTask(): Task {
      return Task(
          uid = uid,
          title = title,
          description = description,
          isCompleted = isCompleted,
          startTime = LocalDate.parse(startTime),
          peopleNeeded = peopleNeeded,
          category = category,
          location = location)
    }
  }
}
