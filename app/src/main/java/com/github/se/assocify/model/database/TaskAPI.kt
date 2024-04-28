package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Task
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

class TaskAPI(private val db: SupabaseClient) : SupabaseApi(){
  private val scope = CoroutineScope(Dispatchers.Main);

  fun getTask(id: String, onSuccess: (Task) -> Unit, onFailure: (Exception) -> Unit) {
    scope.launch {
      try {
        val task = db.from("task")
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
        location = location
      )
    }
  }

}