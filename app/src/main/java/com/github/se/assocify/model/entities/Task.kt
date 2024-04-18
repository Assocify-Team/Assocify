package com.github.se.assocify.model.entities

import com.github.se.assocify.ui.util.DateUtil

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
    val name: String = "testName",
    val description: String = "description",
    val isCompleted: Boolean = false,
    val startTime: String = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
    val peopleNeeded: Int = 0,
    val type: String = "Committee",
    val location: String = "Here"
)
