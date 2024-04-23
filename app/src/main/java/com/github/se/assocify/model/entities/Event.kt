package com.github.se.assocify.model.entities

import com.github.se.assocify.model.serializer.DateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

/**
 * @param uid unique identifier of the event
 * @param name name of the event
 * @param description description of the event
 * @param startDate start date of the event
 * @param endDate end date of the event
 * @param guestsOrArtists guests or artists of the event
 * @param location location of the event
*/
@Serializable
data class Event(
    val uid : String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    @Serializable(with = DateTimeSerializer::class)
    val startDate: LocalDateTime,
    @Serializable(with = DateTimeSerializer::class)
    val endDate: LocalDateTime,
    val guestsOrArtists: String,
    val location: String
    )
