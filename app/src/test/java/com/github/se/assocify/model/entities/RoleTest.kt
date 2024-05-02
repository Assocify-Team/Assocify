package com.github.se.assocify.model.entities

import org.junit.Test

class RoleTest {

  @Test
  fun getRoleType() {
    val role = Role("staff")
    assert(role.getRoleType() == RoleType.STAFF)
  }
}
