package com.github.se.assocify.model.entities

data class Event(
    private val startDate: String,
    private val endDate: String,
    private val organizers: List<User>,
    private val staffers: List<User>
) {
  constructor() : this("", "", listOf(), listOf())

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    other as Event
    return startDate == other.startDate
  }

  override fun hashCode(): Int {
    return startDate.hashCode()
  }

  override fun toString(): String {
    return "Event(startDate='$startDate', endDate='$endDate', organizers=$organizers, staffers=$staffers)"
  }

  fun getStartDate(): String {
    return startDate
  }

  fun getEndDate(): String {
    return endDate
  }

  fun getOrganizers(): List<User> {
    return organizers
  }

  fun getStaffers(): List<User> {
    return staffers
  }
}
