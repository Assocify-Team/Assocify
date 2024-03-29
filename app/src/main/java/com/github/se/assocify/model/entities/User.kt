package com.github.se.assocify.model.entities

class User(val uid: String,
                val name: String,
                role: Role = Role.USER) {

  private var _role: Role


  /** Default constructor used for Firebase deserialization */
  constructor() : this("", "", Role.USER)

  init {
    _role = role
  }
  val role: Role
    get() = _role


  /**
   * Grants a new role to the user
   *
   * @param grantedBy the user granting the role
   * @param newRole the new role to grant
   */
  fun grantRole(grantedBy: User, newRole: Role) {
    if (grantedBy.role.canGrantRoleTo(this,newRole)) {
      _role = newRole
    } else {
      throw Exception("User does not have permission to grant role")
    }
  }

}
