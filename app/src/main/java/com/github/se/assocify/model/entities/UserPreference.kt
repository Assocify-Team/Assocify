package com.github.se.assocify.model.entities

/**
 * Represents the user preferences
 *
 * @param theme the theme of the application
 * @param textSize the text size of the application
 * @param language the language of the application
 */
data class UserPreference(
    var theme: Theme = Theme.SYSTEM,
    var textSize: Int = 15,
    var language: Language = Language.ENGLISH
)

/**
 * Represents the theme of the application
 * LIGHT: Light theme
 * DARK: Dark theme
 * SYSTEM: synchronizes with the system theme
 */
enum class Theme {
    LIGHT, DARK, SYSTEM
}

/**
 * All the languages supported by the application
 */
enum class Language {
    ENGLISH
}