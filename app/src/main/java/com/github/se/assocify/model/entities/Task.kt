package com.github.se.assocify.model.entities

import java.time.LocalDate

/**
 * Data class representing a task that needs to be completed
 *
 * @param uid unique identifier of the task
 * @param name name of the task
 * @param description description of the task
 * @param isCompleted whether the task is completed
 * @param startTime time when the task should start
 * @param peopleNeeded number of people needed to complete the task
 * @param category what the task is for (catering, committee...)
 * @param location location where the task should be completed
 */
data class Task(
    val uid: String = "testUid",
    val title: String = "testName",
    val description: String = "description",
    val isCompleted: Boolean = false,
    val startTime: LocalDate = LocalDate.now(),
    val peopleNeeded: Int = 0,
    val category: String = "Committee",
    val location: String = "Here",
    val eventUid: String = "eventUid"
)
