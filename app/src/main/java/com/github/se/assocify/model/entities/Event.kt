package com.github.se.assocify.model.entities

import com.github.se.assocify.ui.util.DateUtil

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
    val startDate: String = DateUtil.NULL_DATE_STRING,
    val endDate: String = DateUtil.NULL_DATE_STRING,
    val organizers: List<User> = emptyList(),
    val staffers: List<User> = emptyList(),
    val tasks: List<Task> = emptyList(),
)
