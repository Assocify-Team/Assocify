package com.github.se.assocify.model.entities

import java.time.LocalDate

/**
 * Data class representing an event of an association
 *
 * @param uid unique identifier of the event
 * @param name name of the event
 * @param description description of the event
 * @param startDate start date of the event
 * @param endDate end date of the event
 */
data class Event(
    val uid: String,
    val name: String,
    val description: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val guestsOrArtists: List<String>,
    val location: String
)
