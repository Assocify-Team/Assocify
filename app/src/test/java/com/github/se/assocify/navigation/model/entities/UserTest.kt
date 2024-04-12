package com.github.se.assocify.navigation.model.entities

import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UserTest {
  @Test
  fun testGetName() {
    val user = User("1", "John Doe", Role("admin"))
    assert(user.getName() == "John Doe")
  }

  @Test
  fun testGetRole() {
    val user = User("1", "John Doe", Role("admin"))
    assert(user.getRole() == Role("admin"))
  }
}
