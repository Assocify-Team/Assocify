package com.github.se.assocify.model.entities

data class User(val uid: String, private val name: String, private val role: Role) {
  constructor() : this("", "", Role("pending"))

  fun hasRole(role: String): Boolean {
    return this.role.getName().uppercase().contains(role.uppercase())
  }

  fun toggleRole(role: String): User {
    return if (hasRole(role)) {
      User(uid, name, Role("pending"))
    } else {
      User(uid, name, Role(role))
    }
  }
  
  override fun equals(other: Any?): Boolean {
    if (other?.javaClass != this.javaClass) {
      return false
    }
    if (this === other) return true
    other as User
    return uid == other.uid
  }

  override fun hashCode(): Int {
    return uid.hashCode()
  }

  override fun toString(): String {
    return "User(uid='$uid', name='$name', role=$role)"
  }

  fun getName(): String {
    return name
  }

  fun getRole(): Role {
    return role
  }
}
