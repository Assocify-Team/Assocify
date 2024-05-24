package com.github.se.assocify.model.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
 * enumeration of all roles, sorted by importance
 */
enum class RoleType {
  @SerialName("presidency") PRESIDENCY,
  @SerialName("treasury") TREASURY,
  @SerialName("committee") COMMITTEE,
  @SerialName("member") MEMBER,
  @SerialName("staff") STAFF
}

@Serializable
data class PermissionRole(
    val uid: String,
    @SerialName("association_id") val associationId: String,
    val type: RoleType
)
