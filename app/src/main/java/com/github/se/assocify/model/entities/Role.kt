package com.github.se.assocify.model.entities

data class Role(val name: String) {
  constructor() : this("")

  /*
   * enumeration of all roles, sorted by importance
   */
  enum class RoleType {
    PENDING,
    PRESIDENCE,
    TREASURY,
    COMMITTEE,
    MEMBER,
    STAFF
  }

  /**
   * Returns the role type of the role
   *
   * @return the role type of the role
   */
  fun getRoleType(): RoleType {
    return when (name.lowercase()) {
      "pending" -> RoleType.PENDING
      "presidence" -> RoleType.PRESIDENCE
      "treasury" -> RoleType.TREASURY
      "committee" -> RoleType.COMMITTEE
      "member" -> RoleType.MEMBER
      "staff" -> RoleType.STAFF
      else -> RoleType.PENDING
    }
  }

  /**
   * Whether the role is an active role
   *
   * @return whether the role is an active role
   */
  fun isAnActiveRole(): Boolean {
    return getRoleType() != RoleType.PENDING
  }
}
