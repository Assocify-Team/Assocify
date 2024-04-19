package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Association
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * API for interacting with the associations in the database
 *
 * @property db the Firestore database
 */
class AssociationAPI(private val db: SupabaseClient) : SupabaseApi() {
  private val scope = CoroutineScope(Dispatchers.Main)

  /**
   * Gets an association from the database
   *
   * @param id the id of the association to get
   * @param onSuccess called on success with the association
   * @param onFailure called on failure
   * @return the association with the given id
   */
  fun getAssociation(id: Long, onSuccess: (Association) -> Unit, onFailure: (Exception) -> Unit) {
    scope.launch {
      try {
        val assoc =
            db.from("association")
                .select {
                  filter { SupabaseAssociation::uid eq id }
                  limit(1)
                  single()
                }
                .decodeAs<SupabaseAssociation>()
        onSuccess(assoc.toAssociation())
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

  /**
   * Gets all associations from the database
   *
   * @param onSuccess called on success with the list of associations
   * @param onFailure called on failure
   * @return a list of all associations
   */
  fun getAssociations(onSuccess: (List<Association>) -> Unit, onFailure: (Exception) -> Unit) {
    scope.launch {
      try {
        val assoc = db.from("association").select().decodeList<SupabaseAssociation>()
        onSuccess(assoc.map { it.toAssociation() })
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

  /**
   * Adds an association to the database. The UID is discarded, as it is automatically generated.
   *
   * @param association the association to add.
   * @param onSuccess called on success on success with the UID of the new association
   * @param onFailure called on failure
   */
  fun addAssociation(
      association: Association,
      onSuccess: (Long) -> Unit = {},
      onFailure: (Exception) -> Unit
  ) {
    scope.launch {
      try {
        val newUid =
            db.from("association")
                .insert(
                    SupabaseAssociation(
                        null,
                        association.name,
                        association.description,
                        association.creationDate.toString()))
                .decodeAs<SupabaseAssociation>()
                .uid!!
        onSuccess(newUid)
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

  fun editAssociation(
      uid: Long,
      name: String,
      description: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {}

  /**
   * Deletes an association from the database
   *
   * @param id the id of the association to delete
   * @param onSuccess called on success (by default does nothing)
   * @param onFailure called on failure
   */
  fun deleteAssociation(id: Long, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit) {
    scope.launch {
      try {
        db.from("association").delete { filter { SupabaseAssociation::uid eq id } }
        onSuccess()
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

  @Serializable
  private data class SupabaseAssociation(
      val uid: Long? = null,
      val name: String,
      val description: String,
      @SerialName("creation_date") val creationDate: String,
  ) {
    fun toAssociation() = Association(uid!!, name, description, LocalDate.parse(creationDate))
  }
}
