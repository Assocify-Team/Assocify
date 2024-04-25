package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Association
import io.github.jan.supabase.SupabaseClient
import java.time.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class TaskAPI(private val db: SupabaseClient) : SupabaseApi() {

  @Serializable
  private data class SupabaseTask(
      val uid: String? = null,
      val name: String,
      val description: String,
      @SerialName("creation_date") val creationDate: String,
  ) {
    fun toAssociation() = Association(uid!!, name, description, LocalDate.parse(creationDate))
  }
}
