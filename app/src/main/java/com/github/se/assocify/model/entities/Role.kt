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

data class Role(val name: String) {
  constructor() : this("")

  /**
   * Returns the role type of the role
   *
   * @return the role type of the role
   */
  fun getRoleType(): RoleType {
    return when (name.lowercase()) {
      "presidency" -> RoleType.PRESIDENCY
      "treasury" -> RoleType.TREASURY
      "committee" -> RoleType.COMMITTEE
      "member" -> RoleType.MEMBER
      "staff" -> RoleType.STAFF
      else -> RoleType.STAFF
    }
  }
}

@Serializable
data class PermissionRole(
    val uid: String,
    @SerialName("association_id") val associationId: String,
    val type: RoleType
)
