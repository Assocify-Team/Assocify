package com.github.se.assocify.model.database

import android.net.Uri
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.PermissionRole
import com.github.se.assocify.model.entities.RoleType
import com.github.se.assocify.model.entities.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import java.nio.file.Path
import java.time.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * API for interacting with the users in the database
 *
 * @property db the Supabase client
 */
class UserAPI(private val db: SupabaseClient, cachePath: Path) : SupabaseApi() {

  private var userCache = mutableMapOf<String, User>()
  private val imageCacher = ImageCacher(60 * 60_000, cachePath, db.storage["profile-picture"])

  init {
    updateUserCache({}, {})
    updateCurrentUserAssociationCache({}, {})
  }

  /**
   * Updates the user cache
   *
   * @param onSuccess called on success with the list of users
   * @param onFailure called on failure
   */
  fun updateUserCache(onSuccess: (List<User>) -> Unit, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure, tag = "UserAPI[USER_CACHE]") {
      val users = db.from("users").select().decodeList<User>()
      userCache = users.associateBy { it.uid }.toMutableMap()
      onSuccess(users)
    }
  }

  /**
   * Gets a user from the database
   *
   * @param id the id of the user to get
   * @param onSuccess called on success with the user
   */
  fun getUser(id: String, onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit) {
    if (userCache.isNotEmpty()) {
      userCache[id]?.let { onSuccess(it) } ?: onFailure(Exception("User not found"))
    } else {
      updateUserCache(
          { userCache[id]?.let { onSuccess(it) } ?: onFailure(Exception("User not found")) },
          onFailure)
    }
  }
  /**
   * Gets all users from the database
   *
   * @param onSuccess called on success with the list of users
   * @param onFailure called on failure
   */
  fun getAllUsers(onSuccess: (List<User>) -> Unit, onFailure: (Exception) -> Unit) {
    if (userCache.isNotEmpty()) {
      onSuccess(userCache.values.toList())
    } else {
      updateUserCache(onSuccess, onFailure)
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

      userCache[user.uid] = user
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

      val cachedValue = userCache[userId]
      if (cachedValue != null) {
        userCache[userId] = cachedValue.copy(name = newName)
      }
      onSuccess()
    }
  }

  /**
   * Sets the profile picture of a user.
   *
   * @param userId the id of the user to set the profile picture of
   * @param newProfilePicture the URI of the new profile picture
   * @param onSuccess called on success
   * @param onFailure called on failure
   */
  fun setProfilePicture(
      userId: String,
      newProfilePicture: Uri,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    imageCacher.uploadImage(userId, newProfilePicture, onSuccess, onFailure)
  }

  /**
   * Gets the profile picture of a user.
   *
   * @param userId the id of the user to get the profile picture of
   * @param onSuccess called on success with the URI of the profile picture
   * @param onFailure called on failure
   */
  fun getProfilePicture(userId: String, onSuccess: (Uri) -> Unit, onFailure: (Exception) -> Unit) {
    imageCacher.fetchImage(userId, { onSuccess(Uri.fromFile(it.toFile())) }, onFailure)
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
      val invitation =
          db.from("invited")
              .delete {
                filter {
                  eq("user_id", CurrentUser.userUid!!)
                  eq("association_id", associationId)
                }
                select()
              }
              .decodeSingle<JsonObject>()
      db.from("member_of")
          .insert(
              Json.decodeFromString<JsonElement>(
                  """{"user_id": "${CurrentUser.userUid}","role_id": ${invitation["role_id"]}}"""))
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

      userCache.remove(id)
      onSuccess()
    }
  }

  private var currentUserAssociationCache: Map<String, Pair<Association, PermissionRole>>? = null
  private var currentUserId: String? = null

  fun updateCurrentUserAssociationCache(
      onSuccess: (Map<String, Pair<Association, PermissionRole>>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure, tag = "UserAPI[ASSOCIATION_CACHE]") {
      val associations =
          db.from("member_role_association_view")
              .select { filter { Membership::userId eq CurrentUser.userUid!! } }
              .decodeList<Membership>()
              .associate { it.associationId to (it.getAssociation() to it.getRole()) }

      currentUserAssociationCache = associations
      currentUserId = CurrentUser.userUid

      onSuccess(associations)
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
    if (currentUserAssociationCache != null && currentUserId == CurrentUser.userUid) {
      onSuccess(currentUserAssociationCache!!.values.map { it.first })
    } else {
      updateCurrentUserAssociationCache({ onSuccess(it.values.map { it.first }) }, onFailure)
    }
  }

  /**
   * Gets the current user's role in the current association
   *
   * @param onSuccess called on success with the current user's role in the association
   * @param onFailure called on failure
   */
  fun getCurrentUserRole(onSuccess: (PermissionRole) -> Unit, onFailure: (Exception) -> Unit) {
    if (currentUserAssociationCache != null && currentUserId == CurrentUser.userUid) {
      val associationUid = CurrentUser.associationUid!!
      currentUserAssociationCache!![associationUid]?.let { onSuccess(it.second) }
          ?: onFailure(Exception("Association not found"))
    } else {
      updateCurrentUserAssociationCache(
          { map ->
            map[CurrentUser.associationUid]?.let { onSuccess(it.second) }
                ?: onFailure(Exception("Association not found"))
          },
          onFailure)
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
