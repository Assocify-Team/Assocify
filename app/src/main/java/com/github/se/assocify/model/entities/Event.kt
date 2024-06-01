package com.github.se.assocify.model.entities

import java.util.UUID

/**
 * @param uid unique identifier of the event
 * @param name name of the event
 * @param description description of the event
 */
data class Event(
    val uid: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
)
