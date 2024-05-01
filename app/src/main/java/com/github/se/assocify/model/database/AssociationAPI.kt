package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.AssociationMember
import com.github.se.assocify.model.entities.PermissionRole
import com.github.se.assocify.model.entities.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * API for interacting with the associations in the database
 *
 * @property db the Supabase client
 */
class AssociationAPI(private val db: SupabaseClient) : SupabaseApi() {
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
   * Gets a list of applicants to an association.
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
    tryAsync(onFailure) {
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
    tryAsync(onFailure) {
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

  fun getRoles(
      associationId: String,
      onSuccess: (List<PermissionRole>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
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

  fun addRole(role: PermissionRole, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure) {
      addRoleSus(role)
      onSuccess()
    }
  }

  fun inviteUser(member: AssociationMember, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    inviteUser(member.user.uid, member.role, onSuccess, onFailure)
  }

  private suspend fun inviteUserSus(userId: String, role: PermissionRole) {
    db.from("invited")
        .insert(
            Json.decodeFromString<JsonElement>(
                """{"user_id": "$userId","role_id": "${role.uid}", "association_id":  "${role.associationId}"}"""))
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
      }
      onSuccess()
    }
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
