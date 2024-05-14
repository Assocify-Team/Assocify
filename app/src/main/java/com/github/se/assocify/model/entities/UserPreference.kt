package com.github.se.assocify.model.entities

import kotlinx.serialization.Serializable

/**
 * Represents the user preferences
 *
 * @param userUID the user UID
 * @param theme the theme of the application
 * @param textSize the text size of the application
 * @param language the language of the application
 */
data class UserPreference(
    var userUID: String,
    var theme: Theme = Theme.SYSTEM,
    var textSize: Int = 15,
    var language: Language = Language.ENGLISH
)

/**
 * Represents the theme of the application LIGHT: Light theme DARK: Dark theme SYSTEM: synchronizes
 * with the system theme
 */
@Serializable
enum class Theme {
  LIGHT,
  DARK,
  SYSTEM
}

/** All the languages supported by the application */
@Serializable
enum class Language {
  ENGLISH
}
