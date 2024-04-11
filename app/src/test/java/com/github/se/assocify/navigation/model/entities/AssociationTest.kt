package com.github.se.assocify.navigation.model.entities

import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.model.entities.User
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AssociationTest {

  @Test
  fun testNotSameClassEquals() {
    val event = Association()
    assertEquals(false, event.equals("string"))
  }

  @Test
  fun testSameObjectEquals() {
    val association =
        Association("uid1", "name1", "description1", "creationDate1", "status1", listOf(), listOf())
    assert(association.equals(association))
  }

  @Test
  fun testEquals() {
    val association1 =
        Association("uid1", "name1", "description1", "creationDate1", "status1", listOf(), listOf())
    val association2 =
        Association("uid1", "name2", "description2", "creationDate2", "status2", listOf(), listOf())
    assertEquals(association1, association2)
  }

  @Test
  fun testNotEquals() {
    val association1 =
        Association("uid1", "name1", "description1", "creationDate1", "status1", listOf(), listOf())
    val association2 =
        Association("uid2", "name2", "description2", "creationDate2", "status2", listOf(), listOf())
    assertEquals(false, association1 == association2)
  }

  @Test
  fun testHashCode() {
    val association =
        Association("uid1", "name1", "description1", "creationDate1", "status1", listOf(), listOf())
    assertEquals("uid1".hashCode(), association.hashCode())
  }

  @Test
  fun testToString() {
    val association =
        Association("uid1", "name1", "description1", "creationDate1", "status1", listOf(), listOf())
    assertEquals(
        "Association(uid='uid1', name='name1', description='description1', creationDate='creationDate1', status='status1', members=[], events=[])",
        association.toString())
  }

  @Test
  fun testGetName() {
    val association =
        Association("uid1", "name1", "description1", "creationDate1", "status1", listOf(), listOf())
    assertEquals("name1", association.getName())
  }

  @Test
  fun testGetDescription() {
    val association =
        Association("uid1", "name1", "description1", "creationDate1", "status1", listOf(), listOf())
    assertEquals("description1", association.getDescription())
  }

  @Test
  fun testGetCreationDate() {
    val association =
        Association("uid1", "name1", "description1", "creationDate1", "status1", listOf(), listOf())
    assertEquals("creationDate1", association.getCreationDate())
  }

  @Test
  fun testGetStatus() {
    val association =
        Association("uid1", "name1", "description1", "creationDate1", "status1", listOf(), listOf())
    assertEquals("status1", association.getStatus())
  }

  @Test
  fun testGetMembers() {
    val association =
        Association("uid1", "name1", "description1", "creationDate1", "status1", listOf(), listOf())
    assertEquals(listOf<User>(), association.getMembers())
  }

  @Test
  fun testGetEvents() {
    val association =
        Association("uid1", "name1", "description1", "creationDate1", "status1", listOf(), listOf())
    assertEquals(listOf<Event>(), association.getEvents())
  }

  @Test
  fun testConstructor() {
    val association = Association()
    assertEquals("", association.uid)
    assertEquals("", association.getName())
    assertEquals("", association.getDescription())
    assertEquals("", association.getCreationDate())
    assertEquals("", association.getStatus())
    assertEquals(listOf<User>(), association.getMembers())
    assertEquals(listOf<Event>(), association.getEvents())
  }
}
