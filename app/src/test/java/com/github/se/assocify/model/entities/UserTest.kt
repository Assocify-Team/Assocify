package com.github.se.assocify.model.entities

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class UserTest {

    private var userADMIN: User =User("user1", "User 1", Role.ADMIN)
    private var otherUser: User = User("user2", "User 2")


    @Test
    fun testGrantRole() {
        // Try to grant User role to Admin (should fail)
        assertThrows(Exception::class.java) {
            userADMIN.grantRole(otherUser, Role.USER)
        }
        // Try to grant Moderator role to Admin (should fail)
        assertThrows(Exception::class.java) {
            userADMIN.grantRole(otherUser, Role.MODERATOR)
        }
        // Try to grant Admin role to Admin (should fail)
        assertThrows(Exception::class.java) {
            userADMIN.grantRole(otherUser, Role.ADMIN)
        }

        // Grant MODERATOR role to otherUser
        otherUser.grantRole(userADMIN, Role.MODERATOR)
        assertEquals(Role.MODERATOR, otherUser.role)

        // Grant USER role to otherUser
        otherUser.grantRole(userADMIN, Role.USER)
        assertEquals(Role.USER, otherUser.role)

        // Grant ADMIN role to otherUser
        otherUser.grantRole(userADMIN, Role.ADMIN)
        assertEquals(Role.ADMIN, otherUser.role)

        // Try to grant User role to otherUser (Now admin) (should fail)
        assertThrows(Exception::class.java) {
            otherUser.grantRole(userADMIN, Role.USER)
        }
    }

    @Test
    fun testGrantRoleByUserWithoutPermission() {
        val userWithoutPermission = User("user3", "User 3", Role.USER)

        // Try to grant MODERATOR role to otherUser (should fail)
        assertThrows(Exception::class.java) {
            userWithoutPermission.grantRole(otherUser, Role.MODERATOR)
        }
    }
}
