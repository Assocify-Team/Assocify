package com.github.se.assocify.model.entities

data class Association(
    var uid: String,
    val name: String,
    val description: String,
    val creationDate: String,
    val status: String,
    val members: List<User>,
    val events: List<Event>
) {
  constructor() : this("", "", "", "", "", listOf(), listOf())
}
