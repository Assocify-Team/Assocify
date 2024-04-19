package com.github.se.assocify.model.entities

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
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
    val uid: String,
    private val name: String,
    private val description: String,
    private val startDate: LocalDate,
    private val endDate: LocalDate,
    private val guestsOrArtists: List<String>,
    private val location: String
    )