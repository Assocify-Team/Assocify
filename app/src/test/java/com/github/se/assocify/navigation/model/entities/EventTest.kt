package com.github.se.assocify.navigation.model.entities

import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.model.entities.User
import junit.framework.TestCase
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class EventTest {
  @Test
  fun testEquals() {
    val event1 = Event("uid1", "name1", " ", " ", "", listOf(), listOf())
    val event2 = Event("uid1", "name2", " ", " ", "", listOf(), listOf())
    assert(event1 == event2)
  }

  @Test
  fun testNotSameClassEquals() {
    val event = Event()
    TestCase.assertEquals(false, event.equals("string"))
  }

  @Test
  fun testSameObjectEquals() {
    val event = Event("uid1", "name1", " ", " ", "", listOf(), listOf())
    assert(event.equals(event))
  }

  @Test
  fun notEquals() {
    val event1 = Event("uid1", "name1", " ", " ", "", listOf(), listOf())
    val event2 = Event("uid2", "name2", " ", " ", "", listOf(), listOf())
    assert(event1 != event2)
  }

  @Test
  fun testHashCode() {
    val event1 = Event("uid1", "name1", " ", " ", "", listOf(), listOf())
    val event2 = Event("uid1", "name2", " ", " ", "", listOf(), listOf())
    assert(event1.hashCode() == event2.hashCode())
  }

  @Test
  fun testToString() {
    val event = Event("uid1", "name1", " ", " ", "", listOf(), listOf())
    assert(event.toString() == "Event(startDate=' ', endDate='', organizers=[], staffers=[])")
  }

  @Test
  fun testGetStartDate() {
    val event = Event("uid1", "name1", " ", " ", "", listOf(), listOf())
    assert(event.startDate == " ")
  }

  @Test
  fun testGetEndDate() {
    val event = Event("uid1", "name1", " ", " ", "", listOf(), listOf())
    assert(event.endDate == "")
  }

  @Test
  fun testGetOrganizers() {
    val event = Event("uid1", "name1", " ", " ", "", listOf(), listOf())
    assert(event.organizers == listOf<User>())
  }

  @Test
  fun testGetStaffers() {
    val event = Event("uid1", "name1", " ", " ", "", listOf(), listOf())
    assert(event.staffers == listOf<User>())
  }
}
