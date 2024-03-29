package com.github.se.assocify.model.entities

enum class Role(
  private var permissions: Set<Permission>) {

  // The ADMIN role has all permissions
  ADMIN(Permission.entries.toSet()),
  MODERATOR(setOf(Permission.ASSIGN_ROLE)),
  USER(emptySet());

  /**
   * Checks if the role has the permission
   *
   * @param permission the permission to check
   */
  private fun hasPermission(permission: Permission): Boolean {
    return this.permissions.contains(permission)
  }

  /**
   * Checks if the role has all the permissions in the set
   *
   * @param permissions the set of permissions to check
   */
  private fun hasPermissions(permissions: Set<Permission>): Boolean {
    return this.permissions.containsAll(permissions)
  }
  /**
   * Gets the permissions of the role
   *
   * @return the permissions of the role
   */
  fun getPermissions(): Set<Permission> {
    return this.permissions.toMutableSet().toSet()
  }

  /**
   * Grant a role and ensure the granter can't grant more permissions they have
   *
   * @param role the role to grant
   */
  fun canGrantRoleTo(user : User, role: Role): Boolean {
    return this.hasPermission(Permission.ASSIGN_ROLE)
            && this.hasPermissions(role.getPermissions())
            && user.role != this
  }
}
