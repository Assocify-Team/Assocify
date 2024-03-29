package com.github.se.assocify

import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert
import org.junit.Test

class UserAPITest {

  private val db = FirebaseFirestore.getInstance()
  private val userAPI = UserAPI(db)

  @Test
  fun test() {
    val user = User(userAPI.getNewId(), "Test", Role("Admin"))
    userAPI.addUser(user)
    val retrievedUser = userAPI.getUser(user.uid)
    Assert.assertEquals(user, retrievedUser)
    userAPI.deleteUser(user.uid)
    Assert.assertThrows(Exception::class.java) { userAPI.getUser(user.uid) }
  }

  @Test
  fun testAddAllRemoveAll() {
    val allUsers = userAPI.getAllUsers()
    userAPI.deleteAllUsers()

    val shouldEmpty = userAPI.getAllUsers()
    Assert.assertTrue(shouldEmpty.isEmpty())

    userAPI.addAllUsers(allUsers)

    val allUsersAfter = userAPI.getAllUsers()
    Assert.assertEquals(allUsers, allUsersAfter)
  }
}
