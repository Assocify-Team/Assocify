package com.github.se.assocify.navigation.model.entities

import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.model.entities.User
import org.junit.Test

class EventTest {
  @Test
  fun testEquals() {
    val event1 = Event("uid1", "name1", " ", " ", "", listOf(), listOf())
    val event2 = Event("uid1", "name2", " ", " ", "", listOf(), listOf())
    assert(event1.equals(event2))
  }

  @Test
  fun notEquals() {
    val event1 = Event("uid1", "name1", " ", " ", "", listOf(), listOf())
    val event2 = Event("uid2", "name2", " ", " ", "", listOf(), listOf())
    assert(!event1.equals(event2))
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
    assert(event.getStartDate() == " ")
  }

  @Test
  fun testGetEndDate() {
    val event = Event("uid1", "name1", " ", " ", "", listOf(), listOf())
    assert(event.getEndDate() == "")
  }

  @Test
  fun testGetOrganizers() {
    val event = Event("uid1", "name1", " ", " ", "", listOf(), listOf())
    assert(event.getOrganizers().equals(listOf<User>()))
  }

  @Test
  fun testGetStaffers() {
    val event = Event("uid1", "name1", " ", " ", "", listOf(), listOf())
    assert(event.getStaffers().equals(listOf<User>()))
  }
}
