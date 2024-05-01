package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Association
import java.time.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseAssociation(
    val uid: String? = null,
    val name: String,
    val description: String,
    @SerialName("creation_date") val creationDate: String,
) {
  fun toAssociation() = Association(uid!!, name, description, LocalDate.parse(creationDate))
}
