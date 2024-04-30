package com.github.se.assocify.model.entities

/*
 * enumeration of all roles, sorted by importance
 */
enum class RoleType {
  PRESIDENCY,
  TREASURY,
  COMMITTEE,
  MEMBER,
  STAFF
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

// A role as represented in the database.
data class PermissionRole(val uid: String, val associationId: String, val type: RoleType)
