package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Language
import com.github.se.assocify.model.entities.Theme
import com.github.se.assocify.model.entities.UserPreference
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName

class UserPreferenceAPI(private val db : SupabaseClient ) : SupabaseApi() {

    private val collectionName = "user_preference"

    /**
     * Adds a user preference to the database
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
            val userPreference = db.postgrest.from(collectionName)
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
            val userPreferenceSupabase = UserPreferenceSupabase(
                userUID = userUID,
                theme = userPreference.theme.name,
                textSize = userPreference.textSize,
                language = userPreference.language.name
            )
            db.postgrest.from(collectionName)
                .insert(userPreferenceSupabase)
            onSuccess()
        }


    }
}

data class UserPreferenceSupabase(
    @SerialName("user_uid")val userUID: String,
    @SerialName("theme")val theme: String,
    @SerialName("text_size")val textSize: Int,
    @SerialName("language")val language: String
) {
    fun toUserPreference() = UserPreference(
        theme = Theme.valueOf(theme),
        textSize = textSize,
        language = Language.valueOf(language)
    )
}