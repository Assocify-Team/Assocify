package com.github.se.assocify.model.entities

import org.junit.Test

class RoleTest {

  @Test
  fun getRoleType() {
    val role = Role("pending")
    assert(role.getRoleType() == Role.RoleType.PENDING)
  }

  @Test
  fun isAnActiveRole() {
    val roles = Role.RoleType.entries.toTypedArray()
    for (role in roles) {
      val roleEntity = Role(role.name.lowercase())
      if (role == Role.RoleType.PENDING) {
        assert(!roleEntity.isAnActiveRole())
      } else {
        assert(roleEntity.isAnActiveRole())
      }
    }
  }
}
