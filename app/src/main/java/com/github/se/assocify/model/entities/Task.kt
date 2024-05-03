package com.github.se.assocify.model.entities

import java.time.OffsetDateTime

/**
 * Data class representing a task that needs to be completed
 *
 * @param uid unique identifier of the task
 * @param title name of the task
 * @param description description of the task
 * @param isCompleted whether the task is completed
 * @param startTime time when the task should start
 * @param peopleNeeded number of people needed to complete the task
 * @param category what the task is for (catering, committee...)
 * @param location location where the task should be completed
 * @param eventUid unique identifier of the event the task is for
 */
data class Task(
    val uid: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val startTime: OffsetDateTime,
    val peopleNeeded: Int,
    val category: String,
    val location: String,
    val eventUid: String
)
