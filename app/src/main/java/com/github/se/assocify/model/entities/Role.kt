package com.github.se.assocify.model.entities

data class Role(private val name: String) {
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
   * Returns true if the name of the role is the same as the name of the other role
   *
   * @param other the other role to compare
   * @return true if the name of the role is the same as the name of the other role
   */
  override fun equals(other: Any?): Boolean {
    if (other?.javaClass != this.javaClass) {
      return false
    }
    if (this === other) return true
    other as Role
    return name == other.name
  }

  /**
   * Returns the hash code of the name of the role
   *
   * @return the hash code of the name of the role
   */
  override fun hashCode(): Int {
    return name.hashCode()
  }

  /**
   * Returns the string representation of the role
   *
   * @return the string representation of the role
   */
  override fun toString(): String {
    return "Role(name='$name')"
  }

  /**
   * Returns the name of the role
   *
   * @return the name of the role
   */
  fun getName(): String {
    return name
  }
}
