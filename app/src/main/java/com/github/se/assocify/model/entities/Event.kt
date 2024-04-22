package com.github.se.assocify.model.entities

import kotlinx.serialization.Serializable
import java.time.LocalDate

/**
 * @param uid unique identifier of the event
 * @param name name of the event
 * @param description description of the event
 * @param startDate start date of the event
 * @param endDate end date of the event
 * @param guestsOrArtists guests or artists of the event
 * @param location location of the event
*/

data class Event(
    val uid: Int,
    val name: String,
    val description: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val guestsOrArtists: List<String>,
    val location: String
    )