package com.github.se.assocify.model.database

import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.PermissionRole
import com.github.se.assocify.model.entities.RoleType
import com.github.se.assocify.model.entities.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import java.time.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * API for interacting with the users in the database
 *
 * @property db the Supabase client
 */
class UserAPI(private val db: SupabaseClient) : SupabaseApi() {

  /**
   * Gets a user from the database
   *
   * @param id the id of the user to get
   * @param onSuccess called on success with the user
   * @return the user with the given id
   */
  fun getUser(id: String, onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure) {
      val user =
          db.from("users")
              .select {
                filter { User::uid eq id }
                limit(1)
                single()
              }
              .decodeAs<User>()
      onSuccess(user)
    }
  }
  /**
   * Gets all users from the database
   *
   * @param onSuccess called on success with the list of users
   * @param onFailure called on failure
   * @return a list of all users
   */
  fun getAllUsers(onSuccess: (List<User>) -> Unit, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure) {
      val users = db.from("users").select().decodeList<User>()
      onSuccess(users)
    }
  }

  /**
   * Adds a user to the database
   *
   * @param user the user to add/edit
   * @param onSuccess called on success (by default does nothing)
   * @param onFailure called on failure
   */
  fun addUser(user: User, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure) {
      db.from("users").insert(user)
      onSuccess()
    }
  }

  /**
   * Updates the display name of a user.
   *
   * @param userId the id of the user to update
   * @param newName the new display name
   * @param onSuccess called on success
   * @param onFailure called on failure
   */
  fun setDisplayName(
      userId: String,
      newName: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      db.from("users").update({ User::name setTo newName }) { filter { User::uid eq userId } }
      onSuccess()
    }
  }

  /**
   * Requests to join an association.
   *
   * @param associationId the association that the current user wants to join
   * @param onSuccess called on success
   * @param onFailure called on failure
   */
  fun requestJoin(associationId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure) {
      db.from("applicant")
          .insert(
              Json.decodeFromString<JsonElement>(
                  """{"association_id": "$associationId", "user_id": "${CurrentUser.userUid}"}"""))
      onSuccess()
    }
  }

  /**
   * Accepts an invitation from an association.
   *
   * @param associationId the association that the current user accepts the invitation from
   * @param onSuccess called on success
   * @param onFailure called on failure
   */
  fun acceptInvitation(
      associationId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      db.from("invited").delete {
        filter {
          eq("user_id", CurrentUser.userUid!!)
          eq("association_id", associationId)
        }
      }
      onSuccess()
    }
  }

  /**
   * Gets all invitations for the current user
   *
   * @param onSuccess called on success with a list of all associations and the given role. It is
   *   guaranteed that each association appears only once
   * @param onFailure called on failure
   */
  fun getInvitations(
      onSuccess: (List<Pair<PermissionRole, Association>>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      onSuccess(
          db.from("invited")
              .select(
                  Columns.raw(
                      """
                            user_id,
                            role (*),
                            association (*)
                        """
                          .trimIndent()
                          .filter { it != '\n' })) {
                    filter { Invitation::userId eq CurrentUser.userUid }
                  }
              .decodeList<Invitation>()
              .map { it.role to it.association.toAssociation() })
    }
  }

  @Serializable
  private data class Invitation(
      @SerialName("user_id") val userId: String,
      val role: PermissionRole,
      val association: SupabaseAssociation
  )

  /**
   * Deletes a user from the database
   *
   * @param id the id of the user to delete
   * @param onSuccess called on success (by default does nothing)
   * @param onFailure called on failure
   */
  fun deleteUser(id: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure) {
      db.from("users").delete { filter { User::uid eq id } }
      onSuccess()
    }
  }

  /**
   * Gets the associations that the current user is a part of
   *
   * @param onSuccess called on success with the list of associations
   * @param onFailure called on failure
   */
  fun getCurrentUserAssociations(
      onSuccess: (List<Association>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      val associations =
          db.from("member_role_association_view")
              .select { filter { Membership::userId eq CurrentUser.userUid!! } }
              .decodeList<Membership>()
      onSuccess(associations.map { it.getAssociation() })
    }
  }

  /**
   * Gets the current user's role in the current association
   *
   * @param onSuccess called on success with the current user's role in the association
   * @param onFailure called on failure
   */
  fun getCurrentUserRole(onSuccess: (PermissionRole) -> Unit, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure) {
      val membership =
          db.from("member_role_association_view")
              .select {
                filter {
                  Membership::userId eq CurrentUser.userUid!!
                  Membership::associationId eq CurrentUser.associationUid!!
                }
              }
              .decodeSingle<Membership>()
      onSuccess(membership.getRole())
    }
  }

  @Serializable
  private data class Membership(
      @SerialName("user_id") val userId: String,
      @SerialName("role_id") val roleId: String,
      @SerialName("association_id") val associationId: String,
      @SerialName("type") val type: RoleType,
      @SerialName("association_name") val associationName: String,
      @SerialName("association_description") val associationDescription: String,
      @SerialName("association_creation_date") val associationCreationDate: String,
  ) {
    fun getAssociation(): Association {
      return Association(
          associationId,
          associationName,
          associationDescription,
          LocalDate.parse(associationCreationDate),
      )
    }

    fun getRole(): PermissionRole {
      return PermissionRole(roleId, associationId, type)
    }
  }
}
