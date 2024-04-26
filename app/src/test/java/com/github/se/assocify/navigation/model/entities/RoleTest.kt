package com.github.se.assocify.navigation.model.entities

import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.RoleType
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RoleTest {

  @Test
  fun testGetRoleType() {
    RoleType.entries.forEach { roleType ->
      val role = Role(roleType.name.lowercase())
      assert(role.getRoleType() == roleType)
    }
  }
}
