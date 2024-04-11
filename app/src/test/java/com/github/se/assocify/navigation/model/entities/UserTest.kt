package com.github.se.assocify.navigation.model.entities

import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import junit.framework.TestCase
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UserTest {

  @Test
  fun testEquals() {
    val user1 = User("1", "John Doe", Role("admin"))
    val user2 = User("1", "Jane Doe", Role("admin"))
    assert(user1 == user2)
  }

  @Test
  fun testSameObjectEquals() {
    val user = User("1", "John Doe", Role("admin"))
    assert(user.equals(user))
  }

  @Test
  fun testNotSameClassEquals() {
    val user = User()
    TestCase.assertEquals(false, user.equals("string"))
  }

  @Test
  fun notEquals() {
    val user1 = User("1", "John Doe", Role("admin"))
    val user2 = User("2", "Jane Doe", Role("admin"))
    assert(user1 != user2)
  }

  @Test
  fun testHashCode() {
    val user1 = User("1", "John Doe", Role("admin"))
    val user2 = User("1", "Jane Doe", Role("admin"))
    assert(user1.hashCode() == user2.hashCode())
  }

  @Test
  fun testToString() {
    val user = User("1", "John Doe", Role("admin"))
    assert(user.toString() == "User(uid='1', name='John Doe', role=Role(name='admin'))")
  }

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
