package com.github.se.assocify.model.entities

import java.util.UUID
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import java.sql.Timestamp
import java.time.OffsetDateTime
import java.time.OffsetTime

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
    val uid: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime,
    val guestsOrArtists: String,
    val location: String
)
