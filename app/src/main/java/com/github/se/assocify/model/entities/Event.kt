package com.github.se.assocify.model.entities

data class Event(
    val uid: String = "",
    private val name: String = "",
    private val description: String = "",
    private val startDate: String,
    private val endDate: String,
    private val organizers: List<User>,
    private val staffers: List<User>,
    private val tasks: List<Task>,
) {
  constructor() : this("", "", "", "", "", listOf(), listOf(), listOf())

  override fun equals(other: Any?): Boolean {
    if (other?.javaClass != this.javaClass) {
      return false
    }
    if (this === other) return true
    other as Event
    return uid == other.uid
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
