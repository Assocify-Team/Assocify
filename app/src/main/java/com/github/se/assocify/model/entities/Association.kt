package com.github.se.assocify.model.entities

data class Association(
    val uid: String,
    private val name: String,
    private val description: String,
    private val creationDate: String,
    private val status: String,
    private val members: List<User>,
    private val events: List<Event>
) {
  constructor() : this("", "", "", "", "", listOf(), listOf())

  override fun equals(other: Any?): Boolean {
    if (other?.javaClass != this.javaClass) {
      return false
    }
    if (this === other) return true
    other as Association
    return uid == other.uid
  }

  override fun hashCode(): Int {
    return uid.hashCode()
  }

  override fun toString(): String {
    return "Association(uid='$uid', name='$name', description='$description', creationDate='$creationDate', status='$status', members=$members, events=$events)"
  }

  fun getName(): String {
    return name
  }

  fun getDescription(): String {
    return description
  }

  fun getCreationDate(): String {
    return creationDate
  }

  fun getStatus(): String {
    return status
  }

  fun getMembers(): List<User> {
    return members
  }

  fun getEvents(): List<Event> {
    return events
  }
}
