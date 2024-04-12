package com.github.se.assocify.model.entities

data class User(val uid: String, private val name: String, private val role: Role) {
  constructor() : this("", "", Role("pending"))

  fun hasRole(role: String): Boolean {
    return this.role.name.uppercase().contains(role.uppercase())
  }

  fun toggleRole(role: String): User {
    return if (hasRole(role)) {
      User(uid, name, Role("pending"))
    } else {
      User(uid, name, Role(role))
    }
  }

  fun getName(): String {
    return name
  }

  fun getRole(): Role {
    return role
  }
}
