package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.AssociationMember
import com.github.se.assocify.model.entities.PermissionRole
import com.github.se.assocify.model.entities.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * API for interacting with the associations in the database
 *
 * @property db the Supabase client
 */
class AssociationAPI(private val db: SupabaseClient) : SupabaseApi() {
  private var associationCache = mapOf<String, Association>()

  private fun fillCache(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure, tag = "fillCache") {
      val assoc = db.from("association").select().decodeList<SupabaseAssociation>()
      associationCache = assoc.associateBy { it.uid!! }.mapValues { it.value.toAssociation() }
      onSuccess()
    }
  }

  init {
    fillCache({}, {}) // Try and fill the cache as quickly as possible
  }

  /**
   * Gets an association from the database
   *
   * @param id the id of the association to get
   * @param onSuccess called on success with the association
   * @param onFailure called on failure
   * @return the association with the given id
   */
  fun getAssociation(id: String, onSuccess: (Association) -> Unit, onFailure: (Exception) -> Unit) {
    val getFromCache = {
      val value = associationCache[id]
      if (value != null) {
        onSuccess(value)
      } else {
        onFailure(Exception("Association not found"))
      }
    }

    if (associationCache.isNotEmpty()) {
      getFromCache()
    } else {
      fillCache(getFromCache, onFailure)
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
    if (associationCache.isNotEmpty()) {
      onSuccess(associationCache.values.toList())
    } else {
      fillCache({ onSuccess(associationCache.values.toList()) }, onFailure)
    }
  }

  /**
   * Checks if the association name is valid. This means that the name is not empty and is not
   * already taken. This function does not send any requests to the database, because it might be
   * called multiple times in quick succession. As such, call `updateCache` before calling this if
   * you want to ensure the cache is up to date.
   *
   * @param name the name to check
   * @return true if the name is valid, false otherwise
   */
  fun associationNameValid(name: String): Boolean {
    val trimmed = name.trim()
    return name.isNotBlank() && associationCache.values.none { it.name == trimmed }
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
    tryAsync(onFailure, tag = "addAssociation") {
      db.from("association")
          .insert(
              SupabaseAssociation(
                  association.uid,
                  association.name,
                  association.description,
                  association.creationDate.toString()))
      onSuccess()
    }
  }

  /**
   * Edits an association in the database.
   *
   * @param uid the id of the association to edit
   * @param name the new name of the association
   * @param description the new description of the association
   * @param onSuccess called on success
   * @param onFailure called on failure
   */
  fun editAssociation(
      uid: String,
      name: String,
      description: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure, tag = "editAssociation") {
      db.from("association").update({
        SupabaseAssociation::name setTo name
        SupabaseAssociation::description setTo description
      }) {
        filter { SupabaseAssociation::uid eq uid }
      }
      onSuccess()
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
    tryAsync(onFailure, tag = "deleteAssociation") {
      db.from("association").delete { filter { SupabaseAssociation::uid eq id } }
      onSuccess()
    }
  }

  /**
   * Gets a list of applicants to an association. Not cached, as it is currently not used.
   *
   * @param associationId the association to get applicants for
   * @param onSuccess called on success with the list of applicants
   * @param onFailure called on failure
   */
  fun getApplicants(
      associationId: String,
      onSuccess: (List<User>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure, tag = "getApplicants") {
      val applicants =
          db.from("applicant")
              .select(
                  Columns.raw(
                      """
                        association_id,
                        users (
                            *
                        )
                    """
                          .trimIndent()
                          .filter { it != '\n' })) {
                    filter { Applicant::associationId eq associationId }
                  }
              .decodeList<Applicant>()
              .map { it.user }
      onSuccess(applicants)
    }
  }

  /**
   * Accepts an applicant to an association.
   *
   * @param userId the user to accept
   * @param role the role to give the user
   * @param onSuccess called on success
   * @param onFailure called on failure
   */
  fun acceptUser(
      userId: String,
      role: PermissionRole,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure, tag = "acceptUser") {
      if (db.from("applicant")
          .delete {
            filter {
              eq("user_id", userId)
              eq("association_id", role.associationId)
            }
            count(Count.EXACT)
          }
          .countOrNull()!! == 0L) {
        throw Exception("User is not an applicant")
      } else {
        db.from("member_of")
            .insert(
                Json.decodeFromString<JsonElement>(
                    """{"user_id": "$userId","role_id": "${role.uid}"}"""))
        onSuccess()
      }
    }
  }

  /**
   * Gets the roles of an association. Not cached, because it is currently only requested once per
   * association.
   *
   * @param associationId the association to get the roles for
   * @param onSuccess called on success with the list of roles
   * @param onFailure called on failure
   */
  fun getRoles(
      associationId: String,
      onSuccess: (List<PermissionRole>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure, tag = "getRoles") {
      val roles =
          db.from("role")
              .select { filter { PermissionRole::associationId eq associationId } }
              .decodeList<PermissionRole>()
      onSuccess(roles)
    }
  }

  private suspend fun addRoleSus(role: PermissionRole) {
    val supabaseRole = SupabaseRole(role.uid, role.associationId, role.type.name.lowercase())
    db.from("role").insert(supabaseRole)
  }

  /**
   * Adds a role to the association.
   *
   * @param role the role to add
   * @param onSuccess called on success
   * @param onFailure called on failure
   */
  fun addRole(role: PermissionRole, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure) {
      addRoleSus(role)
      onSuccess()
    }
  }

  /**
   * Invites a user to the association with a specific role.
   *
   * @param member the member to invite
   * @param onSuccess called on success
   * @param onFailure called on failure
   */
  fun inviteUser(member: AssociationMember, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    inviteUser(member.user.uid, member.role, onSuccess, onFailure)
  }

  private suspend fun inviteUserSus(userId: String, role: PermissionRole) {
    db.from("invited")
        .insert(
            Json.decodeFromString<JsonElement>(
                """{"user_id": "$userId","role_id": "${role.uid}", "association_id": "${role.associationId}"}"""))
  }

  /**
   * Invites a user to the association with a specific role.
   *
   * @param userId the user to invite
   * @param role the role to give the user
   */
  fun inviteUser(
      userId: String,
      role: PermissionRole,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      inviteUserSus(userId, role)
      onSuccess()
    }
  }

  /**
   * Initializes an association with roles and users. It is guaranteed that the roles will be
   * successfully first, then the users. Fails if the association doesn't exist in the database, so
   * it must be created first (via `addAssociation`).
   *
   * @param roles the roles to add
   * @param users the users to invite
   * @param onSuccess called on success
   * @param onFailure called on failure
   */
  fun initAssociation(
      roles: Collection<PermissionRole>,
      users: Collection<AssociationMember>,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      for (role in roles) {
        addRoleSus(role)
      }
      for (user in users) {
        inviteUserSus(user.user.uid, user.role)
        // TODO: add the UI to accept invitations, remove this function
        acceptInvitationSus(user.user.uid, user.role.associationId)
      }
      onSuccess()
    }
  }

  private suspend fun acceptInvitationSus(
      userId: String,
      associationId: String,
  ) {
    val invitation =
        db.from("invited")
            .delete {
              filter {
                eq("user_id", userId)
                eq("association_id", associationId)
              }
              select()
            }
            .decodeSingle<JsonObject>()
    db.from("member_of")
        .insert(
            Json.decodeFromString<JsonElement>(
                """{"user_id": "$userId","role_id": ${invitation["role_id"]}}"""))
  }

  @Serializable
  private data class Applicant(
      @SerialName("association_id") val associationId: String,
      @SerialName("users") val user: User
  )

  @Serializable
  private data class SupabaseRole(
      val uid: String,
      @SerialName("association_id") val associationId: String,
      val type: String
  )
}
