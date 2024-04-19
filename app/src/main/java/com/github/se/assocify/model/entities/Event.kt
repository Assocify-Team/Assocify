package com.github.se.assocify.model.entities

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

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
