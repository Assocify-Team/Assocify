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
 * @param organizers list of users who are organizing the event
 * @param staffers list of users who are staffing the event
 * @param tasks list of tasks that need to be completed for the event
 */
data class Event(
    val uid: String = "testUid",
    val name: String = "testName",
    val description: String = "description",
    val startDate: String = LocalDate.now().toString(),
    val endDate: String = LocalDate.now().plusDays(1).toString(),
    val organizers: List<User> = emptyList(),
    val staffers: List<User> = emptyList(),
    val tasks: List<Task> = emptyList(),
)
