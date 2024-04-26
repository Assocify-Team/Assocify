package com.github.se.assocify.model.entities

import kotlinx.serialization.Serializable

@Serializable
data class User(val uid: String, val name: String, val email: String? = null) {
  constructor() : this("", "")
}

data class AssociationMember(
    val uid: String,
    val associationId: String,
    val name: String,
    val role: Role
)
