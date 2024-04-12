package com.github.se.assocify.model.entities

data class Role(val name: String) {
  constructor() : this("")

  enum class RoleType {
    PENDING,
    PRESIDENCE,
    TREASURY,
    COMMITTEE,
    MEMBER,
    STAFF
  }

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
}
