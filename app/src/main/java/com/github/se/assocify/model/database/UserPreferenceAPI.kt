package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Language
import com.github.se.assocify.model.entities.Theme
import com.github.se.assocify.model.entities.UserPreference
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class UserPreferenceAPI(private val db: SupabaseClient) : SupabaseApi() {

  private val collectionName = "user_preference"

  /**
   * Get a user preference to the database
   *
   * @param userUID the user preference to add
   * @param onSuccess called on success
   * @param onFailure called on failure
   */
  fun getUserPreference(
      userUID: String,
      onSuccess: (UserPreference) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      val userPreference =
          db.postgrest
              .from(collectionName)
              .select {
                filter { UserPreferenceSupabase::userUID eq userUID }
                limit(1)
                single()
              }
              .decodeAs<UserPreferenceSupabase>()
      onSuccess(userPreference.toUserPreference())
    }
  }

  /**
   * Adds a user preference to the database
   *
   * @param userPreference the user preference to add
   * @param onSuccess called on success
   * @param onFailure called on failure
   */
  fun addUserPreference(
      userUID: String,
      userPreference: UserPreference,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      val userPreferenceSupabase =
          UserPreferenceSupabase(
              userUID = userUID,
              theme = userPreference.theme,
              textSize = userPreference.textSize,
              language = userPreference.language)
      db.postgrest.from(collectionName).insert(userPreferenceSupabase)
      onSuccess()
    }
  }

  /**
   * Updates a user preference in the database
   *
   * @param userUID the user preference to update
   * @param userPreference the user preference to update
   * @param onSuccess called on success
   * @param onFailure called on failure
   */
  fun updateUserPreference(
      userUID: String,
      userPreference: UserPreference,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      db.from(collectionName).update({
        UserPreferenceSupabase::theme setTo userPreference.theme
        UserPreferenceSupabase::textSize setTo userPreference.textSize
        UserPreferenceSupabase::language setTo userPreference.language
      }) {
        filter { UserPreferenceSupabase::userUID eq userUID }
      }
      onSuccess()
    }
  }
}

@Serializable
data class UserPreferenceSupabase(
    @SerialName("user_uid") val userUID: String,
    @SerialName("theme") val theme: Theme,
    @SerialName("text_size") val textSize: Int,
    @SerialName("language") val language: Language
) {
  fun toUserPreference() =
      UserPreference(userUID = userUID, theme = theme, textSize = textSize, language = language)
}
