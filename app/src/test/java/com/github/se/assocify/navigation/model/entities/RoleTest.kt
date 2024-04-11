package com.github.se.assocify.navigation.model.entities

import com.github.se.assocify.model.entities.Role
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RoleTest {

  @Test
  fun testEquals() {
    val role1 = Role("admin")
    val role2 = Role("admin")
    assert(role1 == role2)
  }

  @Test
  fun notEquals() {
    val role1 = Role("admin")
    val role2 = Role("staff")
    assert(role1 != role2)
  }

  @Test
  fun testHashCode() {
    val role1 = Role("admin")
    val role2 = Role("admin")
    assert(role1.hashCode() == role2.hashCode())
  }

  @Test
  fun testToString() {
    val role = Role("admin")
    assert(role.toString() == "Role(name='admin')")
  }

  @Test
  fun testGetName() {
    val role = Role("admin")
    assert(role.getName() == "admin")
  }
}
