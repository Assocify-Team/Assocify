package com.github.se.assocify.model.entities

data class Event(
    val uid: String = "",
    val name: String = "",
    val description: String = "",
    val startDate: String,
    val endDate: String,
    val organizers: List<User>,
    val staffers: List<User>,
    val tasks: List<Task>,
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
}
