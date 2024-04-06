package com.github.se.assocify.model.entities

data class Event(
    val startDate: String,
    val endDate: String,
    val organizers: List<User>,
    val staffers: List<User>
) {
  constructor() : this("", "", listOf(), listOf())
}
