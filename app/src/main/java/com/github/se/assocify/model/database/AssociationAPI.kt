package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * API for interacting with the associations in the database
 *
 * @property db the Supabase client
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
  fun getAssociation(id: String, onSuccess: (Association) -> Unit, onFailure: (Exception) -> Unit) {
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
   * Adds an association to the database.
   *
   * @param association the association to add.
   * @param onSuccess called on success on success with the UID of the new association
   * @param onFailure called on failure
   */
  fun addAssociation(
      association: Association,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit
  ) {
    scope.launch {
      try {
        db.from("association")
            .insert(
                SupabaseAssociation(
                    association.uid,
                    association.name,
                    association.description,
                    association.creationDate.toString()))
        onSuccess()
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

  fun editAssociation(
      uid: String,
      name: String,
      description: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    scope.launch {
      try {
        db.from("association").update({
          SupabaseAssociation::name setTo name
          SupabaseAssociation::description setTo description
        }) {
          filter { SupabaseAssociation::uid eq uid }
        }
        onSuccess()
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

  /**
   * Deletes an association from the database
   *
   * @param id the id of the association to delete
   * @param onSuccess called on success (by default does nothing)
   * @param onFailure called on failure
   */
  fun deleteAssociation(id: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit) {
    scope.launch {
      try {
        db.from("association").delete { filter { SupabaseAssociation::uid eq id } }
        onSuccess()
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

  /**
   * Gets all associations from the database that match the filter
   *
   * @param onSuccess called on success with the list of associations
   * @param onFailure called on failure
   * @param filter the filter to apply to the associations
   * @return a list of all associations that match the filter
   */
  fun getFilteredAssociations(
      onSuccess: (List<Association>) -> Unit,
      onFailure: (Exception) -> Unit,
      filter: (Association) -> Boolean
  ) {
    scope.launch {
      try {
        val assoc = db.from("association").select().decodeList<SupabaseAssociation>()
        onSuccess(assoc.map { it.toAssociation() }.filter { filter(it) })
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

  /**
   * Gets all users from the association that follow the filter
   *
   * @param assocId the id of the association
   * @param onSuccess called on success with the list of users
   * @param onFailure called on failure
   * @param filter the filter to apply to the users
   */
  private fun getFilteredUsers(
      assocId: String,
      onSuccess: (List<User>) -> Unit,
      onFailure: (Exception) -> Unit,
      filter: (User) -> Boolean
  ) {
    scope.launch {
      try {
        val us =
            db.from("association")
                .select(Columns.raw("users(uuid, name, email)"))
                .decodeList<SupabaseUser>()
        onSuccess(us.map { it.toUser() })
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

  /**
   * Gets all pending users from the given association
   *
   * @param assocId the id of the association
   * @param onSuccess called on success with the list of users
   * @param onFailure called on failure
   * @return a list of all pending users
   */
  fun getPendingUsers(
      assocId: String,
      onSuccess: (List<User>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    getFilteredUsers(assocId, onSuccess, onFailure) { user -> user.role == Role("pending") }
  }

  /**
   * Gets all accepted users from the given association
   *
   * @param assocId the id of the association
   * @param onSuccess called on success with the list of users
   * @param onFailure called on failure
   * @return a list of all accepted users
   */
  fun getAcceptedUsers(
      assocId: String,
      onSuccess: (List<User>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    getFilteredUsers(assocId, onSuccess, onFailure) { user -> user.role != Role("pending") }
  }

  /**
   * Gets all users from the given association
   *
   * @param assocId the id of the association
   * @param onSuccess called on success with the list of users
   * @param onFailure called on failure
   * @return a list of all users
   */
  fun getAllUsers(
      assocId: String,
      onSuccess: (List<User>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    getFilteredUsers(assocId, onSuccess, onFailure) { true }
  }

  @Serializable
  private data class SupabaseAssociation(
      val uid: String? = null,
      val name: String,
      val description: String,
      @SerialName("creation_date") val creationDate: String,
  ) {
    fun toAssociation() = Association(uid!!, name, description, LocalDate.parse(creationDate))
  }

  @Serializable
  private data class SupabaseUser(
      val uuid: String,
      val name: String,
      val description: String,
  ) {
    fun toUser() = User(uuid, name, description, Role("pending"))
  }
}
